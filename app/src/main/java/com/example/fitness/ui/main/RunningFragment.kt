package com.example.fitness.ui.main

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import com.google.android.gms.location.LocationRequest;
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.fitness.LoginActivity
import com.example.fitness.MainActivity
import com.example.fitness.R
import com.example.fitness.api.RetrofitClient
import com.example.fitness.databinding.FragmentRunningBinding
import com.example.fitness.dto.running.RunningRequest
import com.example.fitness.dto.running.RunningResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RunningFragment : Fragment() {
    private var elapsedTime = 0L // 경과 시간 (초 단위)
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var runnable: Runnable

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var _binding : FragmentRunningBinding? = null
    private val binding : FragmentRunningBinding
        get() = _binding!!

    companion object {
        fun newInstance(): RunningFragment {
            val fragment = RunningFragment()
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
        _binding = FragmentRunningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.apply {
            val stopButton = view.findViewById<ImageView>(R.id.stop_button)
            stopButton.setOnClickListener {
                showCustomDialog()
            }
            val coin = arguments?.getInt("coin")
            view.findViewById<TextView>(R.id.running_coin).text = coin.toString()
            val currentCookie = arguments?.getLong("currentCookie")
            val gifResId = cookiePick(currentCookie)
            if (gifResId != -1) {
                val imageView = view.findViewById<ImageView>(R.id.running_cookie)
                Glide.with(requireContext()).asGif().load(gifResId).into(imageView)
            }

            /**
             * 애니메이션
             */
            val runningBackground1 = runningBackground1 // 첫 번째 배경
            val runningBackground2 = runningBackground2 // 두 번째 배경

            // 첫 번째 배경 애니메이션
            val animation1 = TranslateAnimation(
                0f,             // 시작 X 위치
                -1070f,         // 종료 X 위치 (이미지 너비에 맞게 조정)
                0f,             // 시작 Y 위치
                0f              // 종료 Y 위치
            ).apply {
                duration = 1500
                repeatCount = Animation.INFINITE
                repeatMode = Animation.RESTART
                interpolator = LinearInterpolator()
            }

            // 두 번째 배경 애니메이션
            val animation2 = TranslateAnimation(
                1070f,
                0f,
                0f,
                0f
            ).apply {
                duration = 1500
                repeatCount = Animation.INFINITE
                repeatMode = Animation.RESTART
                interpolator = LinearInterpolator()
            }

            // 애니메이션 시작
            runningBackground1.startAnimation(animation1)
            runningBackground2.startAnimation(animation2)


            /**
             * 타이머
             */
            val stopwatch = view.findViewById<TextView>(R.id.running_time)
            // 스톱워치 업데이트 함수
            runnable = object : Runnable {
                override fun run() {
                    elapsedTime += 1
                    stopwatch.text = formatTime(elapsedTime)
                    handler.postDelayed(this, 1000) // 1초 후 다시 실행
                }
            }
            handler.post(runnable)

            /**
             * 현재 시간/속도
             */
            // 위치 권한 확인 및 위치 업데이트 시작
            if (ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates() // 위치 업데이트 시작
            } else {
                // 위치 권한 요청
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(ACCESS_FINE_LOCATION),
                    1001
                )
            }


        }
    }

    /**
     * 커스텀 팝업 Dialog 띄우기
     */
    private fun showCustomDialog() {
        val dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.running_end_layout)

        val timeText: TextView = dialog.findViewById(R.id.time_user)
        val distanceText: TextView = dialog.findViewById(R.id.distance_user)
        val average_paceText: TextView = dialog.findViewById(R.id.average_pace_user)
        val coinText: TextView = dialog.findViewById(R.id.coin_user)
        val closeButton: ImageButton = dialog.findViewById(R.id.close_button)

        // 팝업 내용 설정
        timeText.text = formatTime(elapsedTime)
        distanceText.text = String.format("%.2f km", totalDistance / 1000)
        average_paceText.text = calculateAveragePace(elapsedTime, totalDistance) // 평균 페이스 표시
        val coinNum = (totalDistance * 10).toInt()
        coinText.text = coinNum.toString()


        // 닫기 버튼 클릭 이벤트
        closeButton.setOnClickListener {
            dialog.dismiss() // 팝업 닫기

            // 메인 화면으로 이동
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish() // 현재 액티비티 종료
        }
        dialog.show() // 팝업 표시

        runningEnd(RunningRequest(totalDistance / 1000, elapsedTime.toInt()))
    }

    private fun calculateAveragePace(elapsedTime: Long, totalDistance: Float): String {
        // 경과 시간(초)와 총 거리(미터)가 모두 양수일 경우 계산
        if (elapsedTime > 0 && totalDistance > 0) {
            val paceInSeconds = elapsedTime / (totalDistance / 1000) // 초/1km
            val minutes = (paceInSeconds / 60).toInt() // 분
            val seconds = (paceInSeconds % 60).toInt() // 초
            return String.format("%d'%02d''", minutes, seconds) // 예: "6'30''"
        }
        return "0'00''" // 데이터가 없는 경우
    }
    // 시간 포맷 함수 (hh:mm:ss)
    @SuppressLint("DefaultLocale")
    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    
    //권한 확인
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(ACCESS_FINE_LOCATION), 1000)
    }

    private lateinit var locationCallback: LocationCallback
    private var previousLocation: Location? = null
    private var totalDistance = 0f // 누적 거리

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest =  LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        locationCallback = object : LocationCallback() {
            @SuppressLint("DefaultLocale")
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                val location = locationResult.lastLocation
                if (location != null) {
                    // 거리 계산
                    if (previousLocation != null) {
                        val distance = previousLocation!!.distanceTo(location) // 두 위치 간 거리(m)
                        totalDistance += distance
                    }
                    previousLocation = location

                    // 속도 계산
                    val speed = location.speed * 3.6 // m/s → km/h

                    // UI 업데이트
                    binding.apply {
                        runningDistance.text = String.format("%.2f km", totalDistance / 1000) // km로 변환
                        if(speed != 0.0){
                            val minPerKm = 60 / speed // km/h를 min/km로 변환
                            val minutes = minPerKm.toInt() // 분
                            val seconds = ((minPerKm - minutes) * 60).toInt() // 초
                            // 6' 11'' 형태로 포맷팅
                            runningSpeed.text = String.format("%d'%02d''", minutes, seconds)
                        }
                        runningRemainDistanace.text = String.format("%.2f km", 5 -totalDistance / 1000)
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun runningEnd(request: RunningRequest) {
        lifecycleScope.launch {
            try {
                val token =
                    requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                        .getString("jwt_token", null)
                if (token != null) {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.instance.runningEnd(token, request)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cookiePick(cookieId: Long?): Int {
        return when (cookieId?.toInt()) {
            1 -> R.drawable.brave_running
            2 -> R.drawable.zombie_running
            3 -> R.drawable.happy_running
            4 -> R.drawable.angel_running
            5 -> R.drawable.butter_cookie_running
            else -> -1
        }
    }
}