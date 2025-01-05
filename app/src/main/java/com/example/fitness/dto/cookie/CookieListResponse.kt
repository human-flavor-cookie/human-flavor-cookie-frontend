package com.example.fitness.dto.cookie

data class CookieListResponse(
    val cookieId: Long,
    val cookieName: String,
    val owned: Boolean,
    val purchasable: Boolean,
    val accumulatedDistance: Float
)