package com.example.fitness

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fitness.api.RetrofitClient
import com.example.fitness.databinding.ActivityLoginBinding
import com.example.fitness.dto.auth.LoginRequest
import com.example.fitness.dto.auth.SignupRequest
import com.example.fitness.ui.main.MainFragment
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener{
            val email = binding.loginEmail.text.toString()
            val pw = binding.loginPw.text.toString()
            if (email.isNotEmpty() && pw.isNotEmpty()) {
                loginUser(LoginRequest(email, pw))
            } else {
                Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(request: LoginRequest) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.login(request)
                if (response.code() == 200) {
                    // JWT 토큰 저장
                    val token = response.body()?.token
                    if (token != null) {
                        saveToken("Bearer $token")
                    }

                    Toast.makeText(this@LoginActivity, "로그인 성공!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else if(response.code() == 400){
                    Toast.makeText(this@LoginActivity, "존재하는 회원이 없습니다.", Toast.LENGTH_SHORT).show()
                }
                else if(response.code() == 401){
                    Toast.makeText(this@LoginActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", token)
        editor.apply()
    }
}