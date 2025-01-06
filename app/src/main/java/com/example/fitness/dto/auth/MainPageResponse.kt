package com.example.fitness.dto.auth

data class MainPageResponse(
    val name: String,
    val coin: Int,
    val distanceToday: Double,
    val goalDistance: Double
)