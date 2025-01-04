package com.example.fitness.ui.cookie

data class CookieItem(
    val name: String,
    val properties: String,
    val distanceWith: String,
    val distanceWithInt: String,
    val imageRes: Int, // 쿠키 이미지 리소스 ID
    val isDisabled :Boolean,
    val distanceCumulated: Float,
    val isSelected : Boolean
)