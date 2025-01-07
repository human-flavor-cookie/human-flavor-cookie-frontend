package com.example.fitness.dto.cookie

data class CookieListResponse(
    val cookieId: Long,
    val cookieName: String,
    val owned: Boolean,
    val cookiePrice: Int,
    val purchasable: Boolean,
    val accumulatedDistance: Float,
    val alive: Boolean,
    val equipped: Boolean
)