package com.example.fitness

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_splash)

        // 스플래시 화면 2초 유지 후 MainActivity로 전환
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // 뒤로 가기 버튼으로 스플래시 화면으로 돌아가지 않도록 finish() 호출
        }, 2000) // 2000ms = 2초
    }
}
