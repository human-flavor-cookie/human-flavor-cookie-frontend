package com.example.fitness.dto.ranking

data class FriendRanking(
    val userName: String, // 사용자 이름
    val friendRank: Int,        // 사용자 랭킹
    val currentCookieId: Long,
    val dailyDistance: Float,
    val consecutiveDays: Int,
    val successStreak: Boolean
)