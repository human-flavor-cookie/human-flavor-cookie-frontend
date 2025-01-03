package com.example.fitness.ui.permission

import android.content.Intent
import android.health.connect.HealthPermissions
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import com.example.fitness.databinding.ActivityPermissionBinding
import com.example.fitness.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionActivity : AppCompatActivity(){
    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissions = setOf(
            HealthPermissions.READ_STEPS
        )

        val permissionController = HealthConnectClient.getOrCreate(this).permissionController
        //permissionController.pe(permissions, this)

    }

    private val permissionRequest = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // 권한 요청 후의 결과 처리
        // 필요한 경우 MainActivity로 돌아가게 함
        startActivity(Intent(this, MainActivity::class.java))
    }



}