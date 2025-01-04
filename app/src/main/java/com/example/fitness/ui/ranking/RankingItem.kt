package com.example.fitness.ui.ranking

data class RankingItem(
    val rank: Int,
    val name: String,
    val distance: String,
    val success: Int,
    val day: String,
    val status: String,
    val imageResource: Int
)
