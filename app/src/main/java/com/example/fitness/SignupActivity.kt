package com.example.fitness

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fitness.api.RetrofitClient
import com.example.fitness.databinding.ActivitySignupBinding
import com.example.fitness.dto.auth.SignupRequest
import com.google.android.gms.common.api.Response
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding 초기화
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            val name = binding.signupName.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPw.text.toString()
            val pwConfirm = binding.signupPwConfirm.text.toString()
            //비밀번호 확인
            if (password != pwConfirm) Toast.makeText(this, "비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            else{
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    signupUser(SignupRequest(name, email, password))
                } else {
                    Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signupUser(request: SignupRequest) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.signup(request)
                if (response.code() == 200) {
                    Toast.makeText(this@SignupActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "회원가입 실패"
                    Toast.makeText(this@SignupActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SignupActivity, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
