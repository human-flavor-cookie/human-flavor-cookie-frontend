package com.example.fitness.dto.ranking

data class DailyRankingItem(
    val userName: String, // 사용자 이름
    val dailyRank: Int,        // 사용자 랭킹
    val currentCookieId: Long,
    val dailyDistance: Float,
    val consecutiveDays: Int,
    val isSuccessStreak: Boolean
)