package com.example.fitness.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.util.TypedValueCompat.pxToDp
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
import com.example.fitness.Constants
import com.example.fitness.R
import com.example.fitness.databinding.FragmentMainBinding
import com.example.fitness.ui.cookie.CookieAdapter
import com.example.fitness.util.CustomToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            binding.mainStep.text = "오늘 총 걸음수 : \n$steps"
            binding.mainGoal.text = String.format("%.3f km", distance / 1000)
            binding.mainPercent.text = String.format("%.1f%%", distance / 10 / 5)

            //TODO: 목표 맞춰 쿠키 이동
            val cookieLayoutParams = binding.mainCookie.layoutParams as LinearLayout.LayoutParams
            binding.mainCookie.setPadding(dpToPx(requireContext(), (distance / 1000 / 5 * 315 - 50).toInt()), 0, 0, 0)
            binding.mainCookie.layoutParams = cookieLayoutParams

            val progressLayoutParams = binding.mainProgress.layoutParams as FrameLayout.LayoutParams
            progressLayoutParams.width = dpToPx(requireContext(), (distance / 1000 / 5 * 315).toInt())
            binding.mainProgress.layoutParams = progressLayoutParams
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
}