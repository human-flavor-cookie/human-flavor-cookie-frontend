package com.example.fitness.dto.auth


data class LoginResponse(
    val token: String,
    val name: String,
    val email: String,
    val coin: Int,
    val target: Float,
    val success: Int,
    val fail: Int,
    val totalKm: Float,
    val totalTime:Int,
    val currentCookie: Long
)
