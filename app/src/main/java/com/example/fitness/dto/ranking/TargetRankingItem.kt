package com.example.fitness.dto.ranking

data class TargetRankingItem(
    val userName: String, // 사용자 이름
    val targetRank: Int,        // 사용자 랭킹
    val currentCookieId: Long,
    val dailyDistance: Float,
    val consecutiveDays: Int,
    val successStreak: Boolean,
    val currentTier: Float
)