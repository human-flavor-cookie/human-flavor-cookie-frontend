package com.example.fitness.ui.cookie

data class CookieItem(
    val name: String,
    val properties: String,
    val distanceWith: String,
    val distanceWithInt: String,
    val imageRes: Int, // 쿠키 이미지 리소스 ID
    val owned :Boolean,
//    val distanceCumulated: Float,
    val purchasable : Boolean,
    val isSelected : Boolean
)