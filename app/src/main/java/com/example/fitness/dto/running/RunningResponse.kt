package com.example.fitness.dto.running

data class RunningResponse(
    val totalDistance: Float,
    val totalDuration: Int,
    val isGoalMet: Boolean,
    val coinsEarned: Int
)