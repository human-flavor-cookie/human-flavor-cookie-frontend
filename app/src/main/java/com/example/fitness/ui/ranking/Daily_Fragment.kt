package com.example.fitness.ui.ranking

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.R
import com.example.fitness.api.RetrofitClient
import com.example.fitness.dto.ranking.DailyRankingResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Daily_Fragment : Fragment(R.layout.fragment_friend) {
    private var rankingList: DailyRankingResponse? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context) // 수직 스크롤

        lifecycleScope.launch{
            rankingList = dailyRanking()
            // 데이터 설정
            val rankingList = rankingList?.allRanks?.map { rank ->
                RankingItem(
                    rank.dailyRank,
                    rank.userName,
                    "${String.format("%.2f", rank.dailyDistance)}km",
                    rank.consecutiveDays,
                    "일째", streakGet(rank.successStreak),
                    cookiePick(rank.currentCookieId)
                )
            } ?: listOf()
            // 어댑터 설정
            val adapter = RankingAdapter(rankingList)
            recyclerView.adapter = adapter
        }
    }

    // 데이터 목록 (예시로 간단한 텍스트 리스트)
    private fun getData(): List<String> {
        return listOf("Tab 2 - Item 1", "Tab 2 - Item 2", "Tab 2 - Item 3")
    }

    private suspend fun dailyRanking(): DailyRankingResponse? {
        return try {
            val token = requireContext()
                .getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                .getString("jwt_token", null)
            token?.let {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.dailyRanking(it)
                }
                Log.d("d", response.body().toString())
                if (response.code() == 200) response.body() else null
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun cookiePick(cookieId: Long): Int {
        return when (cookieId.toInt()) {
            1 -> R.drawable.brave_stand
            2 -> R.drawable.zombie_stand
            3 -> R.drawable.happy_stand
            4 -> R.drawable.angel_stand
            5 -> R.drawable.buttecookie_stand
            else -> -1
        }
    }

    private fun streakGet(successStreak: Boolean): String {
        return when (successStreak) {
            false ->  "실패 중⚡"
            true -> "달리는 중🔥"
        }
    }
}