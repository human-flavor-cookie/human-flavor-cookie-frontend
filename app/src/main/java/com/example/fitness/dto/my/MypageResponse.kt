package com.example.fitness.dto.my

data class MypageResponse(
    val userName: String,
    val consecutiveSuccessDays: Int,
    val consecutiveFailDays: Int,
    val dailyGoal: Float,
    val totalDistance: Float,
    val averagePace: String
)