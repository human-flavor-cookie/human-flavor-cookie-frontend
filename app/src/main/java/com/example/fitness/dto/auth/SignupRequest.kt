package com.example.fitness.dto.auth

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)