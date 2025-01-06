package com.example.fitness.ui.cookie

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.R
import com.example.fitness.api.RetrofitClient
import com.example.fitness.databinding.FragmentCookieBinding
import com.example.fitness.databinding.FragmentMainBinding
import com.example.fitness.dto.cookie.CookieListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cookie

class CookieFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cookieAdapter: CookieAdapter
//    private lateinit var coinTextView: TextView
//    private var _binding : FragmentCookieBinding? = null
//    private val binding : FragmentCookieBinding
//        get() = _binding!!
    private var coin: Int = 0  // coin 값을 받을 변수

    @SuppressLint("DefaultLocale")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cookie, container, false)

        // 나머지 UI 설정 코드
        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.carouselRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // main_coin TextView를 찾아서 사용
        val mainCoinTextView: TextView = view.findViewById(R.id.main_coin)

        lifecycleScope.launch {
            val c = loginMember()
            mainCoinTextView.text = c.toString() // TextView 업데이트
//            // coin 값을 TextView에 업데이트
//            binding.mainCoin.text = coin.toString()  // main_coin의 텍스트에 coin 값 설정
            // 데이터 설정
            val cookie = cookieList() ?: emptyList()
            val cookieList = mutableListOf(
                CookieItem(
                    name = "용감한맛 쿠키",
                    properties = "ㅋ",
                    distanceWith = "함께 뛴 거리",
                    distanceWithInt = String.format("%.3fkm", cookie[0].accumulatedDistance),
                    imageRes = R.drawable.brave_stand,
                    owned = cookie[0].owned,
                    //distanceCumulated = 5f,
                    purchasable = cookie[0].purchasable,
                    isSelected = true
                ),
                CookieItem(
                    name = "좀비맛 쿠키",
                    properties = "질긴 생명력",
                    distanceWith = "함께 뛴 거리",
                    distanceWithInt = String.format("%.3fkm", cookie[1].accumulatedDistance),
                    imageRes = R.drawable.zombie_stand,
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
                    imageRes = R.drawable.happy_stand,
                    owned = cookie[2].owned,
                    //distanceCumulated = 0f,
                    purchasable = cookie[2].purchasable,
                    isSelected = false
                ),
                CookieItem(
                    name = "천사맛 쿠키",
                    properties = "보상 1.2배",
                    distanceWith = "함께 뛴 거리",
                    distanceWithInt = String.format("%.3fkm", cookie[3].accumulatedDistance),
                    imageRes = R.drawable.happy_die,
                    owned = cookie[3].owned,
                    //distanceCumulated = 15f,
                    purchasable = cookie[3].purchasable,
                    isSelected = false
                ),
                CookieItem(
                    name = "버터크림맛 쿠키",
                    properties = "보상 1.2배",
                    distanceWith = "함께 뛴 거리",
                    distanceWithInt = String.format("%.3fkm", cookie[4].accumulatedDistance),
                    imageRes = R.drawable.buttecookie_stand,
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

    @SuppressLint("SetTextI18n")
    private suspend fun loginMember(): Int? {
        var coin: Int? = null

        try {
            Log.d("d", "token1")
            val token = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).getString("jwt_token", null)
            Log.d("d", "token2")
            if (token != null) {
                Log.d("d", "token3")
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.loginMember(token)
                }

                if (response.code() == 200) {
                    Log.d("d", "token4")
                    val memberName = response.body()?.name
                    coin = response.body()?.coin
                    Log.d("d", coin.toString())

                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return coin
    }
}
