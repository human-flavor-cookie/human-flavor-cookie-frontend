package com.example.fitness.api

import com.example.fitness.dto.auth.SignupRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("member/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Map<String, String>>
}