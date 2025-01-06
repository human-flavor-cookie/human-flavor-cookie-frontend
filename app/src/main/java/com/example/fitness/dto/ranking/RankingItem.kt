package com.example.fitness.dto.ranking

data class RankingItem(
    val userName: String, // 사용자 이름
    val rank: Int,        // 사용자 랭킹
    val currentCookieId: Long,
    val totalDistance: Float,
    val consecutiveDays: Int,
    val successStreak: Boolean
)