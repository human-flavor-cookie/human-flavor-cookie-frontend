package com.example.fitness.api

import com.example.fitness.dto.auth.LoginRequest
import com.example.fitness.dto.auth.LoginResponse
import com.example.fitness.dto.auth.MainPageResponse
import com.example.fitness.dto.auth.SignupRequest
import com.example.fitness.dto.cookie.CookieChangeRequestDto
import com.example.fitness.dto.cookie.CookieListResponse
import com.example.fitness.dto.friend.CreateFriendRequest
import com.example.fitness.dto.friend.PendingResponseDto
import com.example.fitness.dto.friend.RespondFriendRequestDto
import com.example.fitness.dto.my.MypageResponse
import com.example.fitness.dto.my.UpdatePassword
import com.example.fitness.dto.ranking.FriendRankingResponse
import com.example.fitness.dto.ranking.DailyRankingResponse
import com.example.fitness.dto.ranking.TargetRankingResponse
import com.example.fitness.dto.running.RunningRequest
import com.example.fitness.dto.running.RunningResponse
import com.example.fitness.dto.running.UpdateTarget
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    //member
    @POST("member/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Map<String, String>>

    @POST("member/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("member/check-email")
    suspend fun checkEmail(@Query("email") email: String): Response<Map<String, Boolean>>

    @GET("member/main-page")
    suspend fun loginMember(@Header("Authorization") token: String): Response<MainPageResponse>

    @GET("member/profile")
    suspend fun myPage(@Header("Authorization") token: String): Response<MypageResponse>

    @POST("member/update-target")
    suspend fun updateTarget(@Header("Authorization") token: String, @Body request: UpdateTarget): Response<Map<String, String>>

    @POST("member/password")
    suspend fun changePassword(@Header("Authorization") token: String, @Body request: UpdatePassword): Response<Map<String, String>>

    //running
    @POST("api/running/end")
    suspend fun runningEnd(@Header("Authorization") token: String, @Body request: RunningRequest): Response<RunningResponse>

    //cookie
    @GET("api/cookie/list")
    suspend fun cookieList(@Header("Authorization") token: String): Response<List<CookieListResponse>>

    @PATCH("api/cookie/change")
    suspend fun cookieChange(@Header("Authorization") token: String, @Body request: CookieChangeRequestDto): Response<Map<String, String>>

    @POST("api/cookie/purchase")
    suspend fun cookiePurchase(@Header("Authorization") token: String, @Body request: CookieChangeRequestDto): Response<Map<String, String>>

    //ranking
    @GET("member/friendranking")
    suspend fun friendRanking(@Header("Authorization") token: String): Response<FriendRankingResponse>

    @GET("member/dailyranking")
    suspend fun dailyRanking(@Header("Authorization") token: String): Response<DailyRankingResponse>

    @GET("member/targetranking")
    suspend fun targetRanking(@Header("Authorization") token: String): Response<TargetRankingResponse>

    //friend
    @POST("friend-requests")
    suspend fun createFriend(@Header("Authorization") token: String, @Body request: CreateFriendRequest): Response<Map<String, String>>

    @GET("friend-requests/received-pending")
    suspend fun friendReceiveList(@Header("Authorization") token: String): Response<List<PendingResponseDto>>

    @POST("friend-requests/respond")
    suspend fun friendRespond(@Header("Authorization") token: String, @Body request: RespondFriendRequestDto): Response<Map<String, String>>
}