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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private var isEmailAvailable: Boolean = false // 이메일 중복확인 상태 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding 초기화
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 회원가입 버튼 비활성화
        binding.signupButton.isEnabled = false

        binding.checkingButton.setOnClickListener {
            lifecycleScope.launch {
                val email = binding.signupEmail.text.toString()
                Log.d("check", email)

                if (email.isEmpty()) {
                    Toast.makeText(this@SignupActivity, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val check = checkEmail(email)
                isEmailAvailable = check // 이메일 중복 확인 결과 저장
                Log.d("check", "중복 확인 결과: $isEmailAvailable")

                // 이메일이 사용 가능하면 회원가입 버튼 활성화
                binding.signupButton.isEnabled = isEmailAvailable
            }
        }

        binding.signupButton.setOnClickListener {
            val name = binding.signupName.text.toString()
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPw.text.toString()
            val pwConfirm = binding.signupPwConfirm.text.toString()

            // 비밀번호 확인
            if (password != pwConfirm) {
                Toast.makeText(this, "비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                signupUser(SignupRequest(name, email, password))
            } else {
                Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signupUser(request: SignupRequest) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.signup(request)
                if (response.code() == 200) {
                    Toast.makeText(this@SignupActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()

                    // LoginActivity로 이동
                    val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                    startActivity(intent)
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

    private suspend fun checkEmail(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.checkEmail(email)
                if (response.code() == 200) {
                    val isAvailable = response.body()?.get("isAvailable") == true
                    withContext(Dispatchers.Main) {
                        if (isAvailable) {
                            Toast.makeText(this@SignupActivity, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@SignupActivity, "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    isAvailable
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "회원가입 실패"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignupActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignupActivity, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                false
            }
        }
    }
}

