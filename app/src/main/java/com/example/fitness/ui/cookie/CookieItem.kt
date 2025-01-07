package com.example.fitness.ui.cookie

data class CookieItem(
    val name: String,
    val properties: String,
    val distanceWith: String,
    val distanceWithInt: String,
    val imageRes: Int, // 쿠키 이미지 리소스 ID
    val owned :Boolean,
    val priceOrConditon: String,
//    val distanceCumulated: Float,
    var purchasable : Boolean,
    var isSelected : Boolean
)