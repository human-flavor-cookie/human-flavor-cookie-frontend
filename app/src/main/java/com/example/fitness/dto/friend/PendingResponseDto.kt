package com.example.fitness.dto.friend

data class PendingResponseDto(
    val friendRequestId: Long,
    val requesterId: Long,
    val requesterName: String,
    val requesterEmail: String
)