package com.example.fitness.ui.my

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fitness.LoginActivity
import com.example.fitness.MainActivity
import com.example.fitness.R
import com.example.fitness.api.RetrofitClient
import com.example.fitness.databinding.FragmentMyBinding
import com.example.fitness.dto.my.MypageResponse
import com.example.fitness.dto.running.UpdateTarget
import com.example.fitness.ui.cookie.CookieFragment
import com.example.fitness.ui.main.MainFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Optional.empty

class MyFragment : Fragment(R.layout.fragment_my) {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트 레이아웃 Inflate
        val view = inflater.inflate(R.layout.fragment_my, container, false)

        // 버튼 참조
        val button = view.findViewById<Button>(R.id.logout_button)
        button.setOnClickListener {
            showPopup()
        }

        val password_change_button = view.findViewById<Button>(R.id.password_change)
        password_change_button.setOnClickListener{
            showPasswordChangePopUp()
        }

        val target_distance_change_button = view.findViewById<Button>(R.id.target_distance_change_button)
        target_distance_change_button.setOnClickListener{
            showTargetDistanceChangePopUp()
        }

        val add_friend_button = view .findViewById<Button>(R.id.add_friend)
        add_friend_button.setOnClickListener{
            showAddFriendPopUp()
        }

        // Mypage 데이터를 가져오는 로직
        lifecycleScope.launch {
            // 데이터 설정
            val userInfoData = loadMypageData()
            view.findViewById<TextView>(R.id.user_name).text = userInfoData?.userName.toString()
            val formattedDistance = String.format("%.1f", userInfoData?.dailyGoal ?: 0.0)
            view.findViewById<TextView>(R.id.distance_day_user).text = "$formattedDistance"+"km"
            val formattedTotalDistance = String.format("%.2f", userInfoData?.totalDistance ?: 0.0)
            view.findViewById<TextView>(R.id.distance_cumulated_user).text = "$formattedTotalDistance"+"km"
            val averageUserPace = userInfoData?.averagePace.toString()
            val min_and_sec = averageUserPace.split(":")
            view.findViewById<TextView>(R.id.running_pace_user).text = min_and_sec[0] + "' " + min_and_sec[1] + "''"
            if (userInfoData?.consecutiveFailDays == 0) {
                view.findViewById<TextView>(R.id.misson_days).text =
                    userInfoData?.consecutiveSuccessDays.toString() + "일 연속 목표 달성 중🔥"
            }
            else view.findViewById<TextView>(R.id.misson_days).text =
                userInfoData?.consecutiveFailDays.toString() + "일 연속 실패⚡⚡⚡"
        }

        return view
    }

    private suspend fun loadMypageData(): MypageResponse? {
        var userInfo: MypageResponse? = null
        try {
            val token = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).getString("jwt_token", null)
            if (token != null) {
                Log.d("d", token.toString())
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.myPage(token)
                }
                Log.d("d", response.toString())
                if (response.code() == 200) {
                    userInfo = response.body()
                }
            }
        } catch (e: Exception) {
            Log.d("d", "에러")
            Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return userInfo
    }

    private fun showPopup() {
        // 팝업 레이아웃 Inflate
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.logout_layout, null)

        // AlertDialog 생성
        val dialog = AlertDialog.Builder(requireContext())

            .setView(popupView)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 팝업 내 닫기 버튼 이벤트 설정
        val closeButton = popupView.findViewById<ImageButton>(R.id.cancel_button)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        val goButton = popupView.findViewById<ImageButton>(R.id.go_button)
        goButton.setOnClickListener {
            // 로그인 화면으로 이동 - 토큰 제거
            val prefs = activity?.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            prefs?.edit()?.apply {
                remove("jwt_token") // jwt_token 삭제
                apply()
            }
            
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish() // 현재 액티비티 종료
        }
        // 팝업 표시
        dialog.show()
    }

    private fun showPasswordChangePopUp() {
        // 팝업 레이아웃 Inflate
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.password_change_layout, null)

        // AlertDialog 생성
        val dialog = AlertDialog.Builder(requireContext())

            .setView(popupView)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val goButton = popupView.findViewById<ImageButton>(R.id.go_button)
        goButton.setOnClickListener{
            // 로그인 화면으로 이동 - 토큰 제거
            val prefs = activity?.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            prefs?.edit()?.apply {
                remove("jwt_token") // jwt_token 삭제
                apply()
            }

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish() // 현재 액티비티 종료
        }

        val cancelButton = popupView.findViewById<ImageButton>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showTargetDistanceChangePopUp() {
        // 팝업 레이아웃 Inflate
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.target_distance_change_layout, null)

        // 이전 목표 거리 가져오기
        val sharedPreferences = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val previousGoal = sharedPreferences.getFloat("previous_goal", 0f)
        // AlertDialog 생성
        val dialog = AlertDialog.Builder(requireContext())

            .setView(popupView)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val goalInput = popupView.findViewById<EditText>(R.id.target_distance)
        val goButton = popupView.findViewById<ImageButton>(R.id.go_button)
        goButton.setOnClickListener{

            val inputText = goalInput.text.toString()
            val goal = inputText.toFloatOrNull()
            if (goal != null && goal > 0) {
                if (goal > previousGoal) {
                    dialog.dismiss()
                    onGoalSet(goal) // 서버에 전송
                    // 새로운 목표를 SharedPreferences에 저장
                    sharedPreferences.edit().putFloat("previous_goal", goal).apply()
                } else {
                    Toast.makeText(requireContext(), "이전 목표보다 큰 값을 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "올바른 값을 입력하세요.", Toast.LENGTH_SHORT).show()
            }

        }

        val cancelButton = popupView.findViewById<ImageButton>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            dialog.dismiss()
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
                            // 최신 데이터 가져와 반영
                            lifecycleScope.launch {
                                val updatedData = loadMypageData()
                                updatedData?.let { data ->
                                    val formattedDistance = String.format("%.1f", data.dailyGoal)
                                    view?.findViewById<TextView>(R.id.distance_day_user)?.text = "$formattedDistance km"
                                }
                            }
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

    private fun showAddFriendPopUp() {
        // 팝업 레이아웃 Inflate
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.add_friend_layout, null)

        // AlertDialog 생성
        val dialog = AlertDialog.Builder(requireContext())
            .setView(popupView)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val emailInput = popupView.findViewById<EditText>(R.id.add_friend)
        val goButton = popupView.findViewById<ImageButton>(R.id.go_button)
        goButton.setOnClickListener{
            val friendEmailInput = emailInput.text.toString()
        }

        val cancelButton = popupView.findViewById<ImageButton>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}