package com.example.fitness.api

import com.example.fitness.dto.auth.LoginRequest
import com.example.fitness.dto.auth.LoginResponse
import com.example.fitness.dto.auth.MainPageResponse
import com.example.fitness.dto.auth.SignupRequest
import com.example.fitness.dto.cookie.CookieListResponse
import com.example.fitness.dto.running.RunningRequest
import com.example.fitness.dto.running.RunningResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    //member
    @POST("member/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Map<String, String>>

    @POST("member/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("member/main-page")
    suspend fun loginMember(@Header("Authorization") token: String): Response<MainPageResponse>

    //running
    @POST("api/running/end")
    suspend fun runningEnd(@Header("Authorization") token: String, @Body request: RunningRequest): Response<RunningResponse>

    //cookie
    @GET("api/cookie/list")
    suspend fun cookieList(@Header("Authorization") token: String): Response<List<CookieListResponse>>
}