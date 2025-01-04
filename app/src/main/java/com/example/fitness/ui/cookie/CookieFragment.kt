package com.example.fitness.ui.cookie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.R
import okhttp3.Cookie

class CookieFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cookieAdapter: CookieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cookie, container, false)

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.carouselRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 데이터 설정
        val cookieList = mutableListOf(
            CookieItem(
                name = "용감한맛 쿠키",
                properties = "ㅋ",
                distanceWith = "함께 뛴 거리",
                distanceWithInt = "50.3km",
                imageRes = R.drawable.brave_cookie_cookietab,
                isDisabled = false,
                distanceCumulated = 5f,
                isSelected = true
            ),
            CookieItem(
                name = "명랑한맛 쿠키",
                properties = "기분이 좋아질 수도",
                distanceWith = "함께 뛴 거리",
                distanceWithInt = "120.3km",
                imageRes = R.drawable.happy_cookie_cookietab,
                isDisabled = true,
                distanceCumulated = 0f,
                isSelected = false
            ),
            CookieItem(
                name = "좀비맛 쿠키",
                properties = "3회 부활가능",
                distanceWith = "함께 뛴 거리",
                distanceWithInt = "0km",
                imageRes = R.drawable.zombie_cookie_cookietab,
                isDisabled = false,
                distanceCumulated = 50f,
                isSelected = false
            ),
            CookieItem(
                name = "천사맛 쿠키",
                properties = "보상 1.2배",
                distanceWith = "함께 뛴 거리",
                distanceWithInt = "0km",
                imageRes = R.drawable.angel_cookie_cookietab,
                isDisabled = false,
                distanceCumulated = 15f,
                isSelected = false
            )

        )

        // 어댑터 설정
        cookieAdapter = CookieAdapter(cookieList)
        recyclerView.adapter = cookieAdapter

        return view
    }
}
