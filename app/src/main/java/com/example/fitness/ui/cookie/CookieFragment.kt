package com.example.fitness.ui.cookie

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.R
import com.example.fitness.api.RetrofitClient
import com.example.fitness.dto.cookie.CookieListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cookie

class CookieFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cookieAdapter: CookieAdapter

    @SuppressLint("DefaultLocale")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cookie, container, false)

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.carouselRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            // 데이터 설정
            val cookie = cookieList() ?: emptyList()
            val cookieList = mutableListOf(
                CookieItem(
                    name = "용감한맛 쿠키",
                    properties = "ㅋ",
                    distanceWith = "함께 뛴 거리",
                    distanceWithInt = String.format("%.3fkm", cookie[0].accumulatedDistance),
                    imageRes = R.drawable.brave_cookie_cookietab,
                    owned = cookie[0].owned,
                    //distanceCumulated = 5f,
                    purchasable = cookie[0].purchasable,
                    isSelected = true
                ),
                CookieItem(
                    name = "좀비맛 쿠키",
                    properties = "3회 부활가능",
                    distanceWith = "함께 뛴 거리",
                    distanceWithInt = String.format("%.3fkm", cookie[1].accumulatedDistance),
                    imageRes = R.drawable.zombie_cookie_cookietab,
                    owned = cookie[1].owned,
                    //distanceCumulated = 50f,
                    purchasable = cookie[1].purchasable,
                    isSelected = false
                ),
                CookieItem(
                    name = "명랑한맛 쿠키",
                    properties = "기분이 좋아질 수도",
                    distanceWith = "함께 뛴 거리",
                    distanceWithInt = String.format("%.3fkm", cookie[2].accumulatedDistance),
                    imageRes = R.drawable.happy_cookie_cookietab,
                    owned = cookie[2].owned,
                    //distanceCumulated = 0f,
                    purchasable = cookie[2].purchasable,
                    isSelected = false
                ),
                CookieItem(
                    name = "천사맛 쿠키",
                    properties = "보상 1.2배",
                    distanceWith = "함께 뛴 거리",
                    distanceWithInt = String.format("%.3fkm", cookie[4].accumulatedDistance),
                    imageRes = R.drawable.angel_cookie_cookietab,
                    owned = cookie[3].owned,
                    //distanceCumulated = 15f,
                    purchasable = cookie[3].purchasable,
                    isSelected = false
                )
                //TODO: 버터맛 쿠키 추가 필요
            )

            // 어댑터 설정
            cookieAdapter = CookieAdapter(requireContext(), cookieList)
            recyclerView.adapter = cookieAdapter
        }

        return view
    }

    private suspend fun cookieList(): List<CookieListResponse>? {
        var cookieList: List<CookieListResponse> ?= null
        try {
            val token = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).getString("jwt_token", null)
            if (token != null) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.cookieList(token)
                }

                if (response.code() == 200) {
                    cookieList = response.body()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return cookieList
    }
}
