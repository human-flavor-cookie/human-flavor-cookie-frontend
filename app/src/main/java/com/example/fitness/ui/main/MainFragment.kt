package com.example.fitness.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.FloorsClimbedRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.Constants
import com.example.fitness.R
import com.example.fitness.api.RetrofitClient
import com.example.fitness.databinding.FragmentMainBinding
import com.example.fitness.dto.auth.MainPageResponse
import com.example.fitness.dto.friend.PendingResponseDto
import com.example.fitness.dto.friend.RespondFriendRequestDto
import com.example.fitness.dto.running.UpdateTarget
import com.example.fitness.ui.ranking.RankingItem
import com.example.fitness.util.CustomToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.LocalTime

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var TAG : String = "MAIN FRAGMENT"
    private var _binding : FragmentMainBinding? = null
    private val binding : FragmentMainBinding
        get() = _binding!!

    private lateinit var healthConnectClient: HealthConnectClient
    private val permissionList = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class),
        HealthPermission.getReadPermission(FloorsClimbedRecord::class)
    )

    private val requestPermissions = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        if (granted.containsAll(permissionList)) {
            Log.d("MAIN ACTIVITY", "requestPermissions success")
            CoroutineScope(Dispatchers.IO).launch {
                readStepsData()
            }
        } else {
            CustomToast.createToast(requireContext(), "건강정보 가져오기에 실패하였습니다.\n권한을 추가하거나 헬스 커넥트 앱을 다운로드 해주세요.")?.show()
            Log.d("MAIN ACTIVITY", "requestPermissions fail")
            activity?.finish()
            openPlayStoreForHealthConnect()
        }
    }

    companion object {
        fun newInstance(): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        // 알림 버튼 클릭 시 다이얼로그 띄우기
        binding.notificationButton.setOnClickListener {
            showNotificationsDialog()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Health Connect
        CoroutineScope(Dispatchers.Main).launch {
            connectHealthData()
        }

    }

    private fun connectHealthData() {
        // 1. Health Connect 앱 유무 확인
        val availabilityStatus = HealthConnectClient.getSdkStatus(requireContext(), Constants.HEALTH_CONNECT_PACKAGE_NAME)
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            CustomToast.createToast(requireContext(), "헬스 커넥트 앱을 다운로드 해주세요")?.show()
            activity?.finish()
            openPlayStoreForHealthConnect()
        }

        // 2. Health connect sdk update 확인
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED) {
            CustomToast.createToast(requireContext(), "헬스 커넥트 업데이트가 필요합니다")?.show()
            activity?.finish()
            openPlayStoreForHealthConnect()
        }

        // 4. Check permissions and run
        healthConnectClient = HealthConnectClient.getOrCreate(requireContext())
        requestPermissions.launch(permissionList)
    }


    @SuppressLint("SetTextI18n", "DefaultLocale")
    private suspend fun readStepsData() {
        val now: LocalDateTime = LocalDateTime.now()
        val startOfDay = LocalDateTime.of(now.toLocalDate(), LocalTime.MIDNIGHT)

        val request = ReadRecordsRequest(
            recordType = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startOfDay, now),
        )

        val response = healthConnectClient.readRecords(request)
        val steps = response.records.sumOf { it.count }

        //걸음 m
        val distanceRequest = ReadRecordsRequest(
            recordType = DistanceRecord::class,
            timeRangeFilter = TimeRangeFilter.between(startOfDay, now),
        )
        val distanceResponse = healthConnectClient.readRecords(distanceRequest)
        val distance = distanceResponse.records.sumOf { it.distance.inMeters }

        CoroutineScope(Dispatchers.Main).launch {
            var dto = loginMember()
            if (dto?.goalDistance == 0.0) {
                showGoalInputDialog { newGoal ->
                    onGoalSet(newGoal)
                }
            }
            binding.mainStep.text = "오늘 총 걸음수 : \n$steps"

            //시작 버튼 클릭
            binding.mainStart.setOnClickListener {
                if(dto != null){
                    val fragment = RunningFragment()
                    val bundle = Bundle()
                    bundle.putInt("coin", dto.coin)
                    bundle.putLong("currentCookie", dto.currentCookieId)
                    fragment.arguments = bundle

                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            }
        }
    }

    private fun openPlayStoreForHealthConnect() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    @SuppressLint("SetTextI18n")
    private suspend fun loginMember(): MainPageResponse? {
        var dto: MainPageResponse? = null

        try {
            val token = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).getString("jwt_token", null)
            if (token != null) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.loginMember(token)
                }

                if (response.code() == 200) {
                    val memberName = response.body()?.name
                    dto = response.body()
                    val goal = response.body()?.goalDistance  //목표
                    binding.mainName.text = "$memberName 님"
                    binding.mainCoin.text = dto?.coin.toString()
                    binding.mainGoal.text = "오늘의 목표: " + String.format("%.1f", goal) + " km"
                    val distanceToday = response.body()?.distanceToday  //오늘 뛴 거리
                    binding.distanceNow.text = "현재: " + String.format("%.1f", distanceToday) + " km"
                    binding.mainPercent.text = goal?.let { distanceToday?.times(100)?.div(it)?.toInt().toString() + " %"}

                    // cookie moving
                    var cookie_progress = distanceToday?.times(315)?.div(goal!!)?.minus(50)?.toInt().toString()

                    val cookieLayoutParams = binding.mainCookie.layoutParams as LinearLayout.LayoutParams
                    binding.mainCookie.setPadding(dpToPx(requireContext(), cookie_progress.toInt()), 0, 0, 0)
                    binding.mainCookie.layoutParams = cookieLayoutParams

                    // pregress bar
                    val bar_progress = distanceToday?.times(315)?.div(goal!!)?.toInt().toString()
                    val progressLayoutParams = binding.mainProgress.layoutParams as FrameLayout.LayoutParams
                    progressLayoutParams.width = dpToPx(requireContext(), bar_progress.toInt())
                    binding.mainProgress.layoutParams = progressLayoutParams

                    // 현재 쿠키
                    var current_cookie = response.body()?.currentCookieId
                    binding.mainCookie.setImageResource(cookiePick(current_cookie))

                    //알람 개수
                    var pendingCount = response.body()?.pendingCount
                    binding.notificationCount.text = pendingCount.toString()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return dto
    }

    private fun cookiePick(cookieId: Long?): Int {
        return when (cookieId?.toInt()) {
            1 -> R.drawable.brave_run_s
            2 -> R.drawable.zombie_run_s
            3 -> R.drawable.happy_run_s
            4 -> R.drawable.angel_run_s
            5 -> R.drawable.buttercookie_run_s
            else -> -1
        }
    }

    private fun showGoalInputDialog(onGoalSet: (Float) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.target_input, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val goalInput = dialogView.findViewById<EditText>(R.id.login_email)
        val submitButton = dialogView.findViewById<ImageButton>(R.id.go_button)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        submitButton.setOnClickListener {

            val inputText = goalInput.text.toString()
            val goal = inputText.toFloatOrNull()

            if (goal != null && goal > 0) {
                dialog.dismiss()
                onGoalSet(goal) //서버에 전송
            } else {
                Toast.makeText(requireContext(), "올바른 값을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun onGoalSet(newGoal: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    .getString("jwt_token", null)

                token?.let {
                    val response = RetrofitClient.instance.updateTarget(token, UpdateTarget(newGoal))

                    if (response.code() == 200) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "목표가 저장되었습니다.", Toast.LENGTH_SHORT).show()

                            val fragmentManager = requireActivity().supportFragmentManager
                            val currentFragment = fragmentManager.findFragmentByTag("MAIN_FRAGMENT_TAG")

                            if (currentFragment != null) {
                                fragmentManager.beginTransaction()
                                    .remove(currentFragment)
                                    .commitNow()
                            }

                            fragmentManager.beginTransaction()
                                .replace(R.id.nav_host_fragment, MainFragment(), "MAIN_FRAGMENT_TAG")
                                .commit()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "목표 저장 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun showNotificationsDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.notification_popup, null)
        val recyclerView: RecyclerView = dialogView.findViewById(R.id.notification_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 다이얼로그와 RecyclerView 업데이트 로직
        fun updateAdapter(dialog: AlertDialog) {
            CoroutineScope(Dispatchers.Main).launch {
                val updatedList = pendingList() ?: emptyList()
                if (updatedList.isNotEmpty()) {
                    val updatedItems = updatedList.map { member ->
                        NotificationAdapter.NotificationItem(
                            member.requesterName,
                            member.requesterEmail,
                            member.friendRequestId
                        )
                    }
                    recyclerView.adapter = NotificationAdapter(
                        context = requireContext(),
                        notifications = updatedItems,
                        onAcceptClick = { notificationItem ->
                            handleFriendResponse(RespondFriendRequestDto(notificationItem.id, "ACCEPT")) { updateAdapter(dialog) }
                        },
                        onRejectClick = { notificationItem ->
                            handleFriendResponse(RespondFriendRequestDto(notificationItem.id, "REJECT")) { updateAdapter(dialog) }
                        }
                    )
                    dialog.show() // 데이터 로드 완료 후 다이얼로그 표시
                } else {
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "친구 요청 목록이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 네트워크 요청 후 다이얼로그 표시
        CoroutineScope(Dispatchers.Main).launch {
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create()

            updateAdapter(dialog) // 데이터 로드 및 다이얼로그 업데이트
        }
    }

    private suspend fun pendingList(): List<PendingResponseDto>? {
        return try {
            val token = context?.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                ?.getString("jwt_token", null)
            if (token != null) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.friendReceiveList(token)
                }
                Log.d("d", response.body().toString())
                if (response.code() == 200) {
                    Log.d("pendingList", "실행")
                    response.body()
                } else {
                    Log.d("pendingList", "뭔가 문제있음")
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun handleFriendResponse(
        request: RespondFriendRequestDto,
        onSuccess: () -> Unit,
    ) {
        lifecycleScope.launch {
            try {
                // 토큰 가져오기
                val token = context?.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    ?.getString("jwt_token", null)
                if (token != null) {
                    // 네트워크 요청 실행
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.instance.friendRespond(token, request)
                    }
                    if (response.code() == 200) {
                        Log.d("handleFriendResponse", "응답 성공: ${response.body()}")
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                Log.e("handleFriendResponse", "네트워크 오류", e)
            }
        }
    }

}