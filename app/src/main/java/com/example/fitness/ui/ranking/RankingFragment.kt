package com.example.fitness.ui.ranking

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.fitness.R
import com.example.fitness.api.RetrofitClient
import com.example.fitness.databinding.FragmentRankingBinding
import com.example.fitness.dto.ranking.AllRankingResponse
import com.example.fitness.dto.ranking.DailyRankingResponse
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RankingFragment : Fragment(R.layout.fragment_ranking) {

    private lateinit var binding: FragmentRankingBinding

    // 랭킹 데이터를 저장할 변수들
    private var rankingList: AllRankingResponse? = null
    private var dailyRankingList: DailyRankingResponse? = null
    private var rankingListAll: List<RankingItem> = listOf()
    private var rankingListDaily: List<RankingItem> = listOf()
    private var rankingListTier: List<RankingItem> = listOf()
    private var rankingListMe: List<RankingItem> = listOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRankingBinding.bind(view)

        setupTabLayout()

        lifecycleScope.launch {
            fetchRankingData()
            updateTabContent(0) // "전체" 탭 초기 데이터 설정
        }
    }

    private suspend fun fetchRankingData() {
        rankingList = ranking()
        dailyRankingList = dailyRanking()

        rankingListAll = rankingList?.top3?.map { rank ->
            RankingItem(
                rank.rank,
                rank.userName,
                "${String.format("%.2f", rank.totalDistance)}km",
                rank.consecutiveDays,
                "일째", "달리는 중🔥",
                cookiePick(rank.currentCookieId)
            )
        } ?: listOf()

        rankingListDaily = dailyRankingList?.top3?.map { rank ->
            RankingItem(
                rank.dailyRank,
                rank.userName,
                "${String.format("%.2f", rank.dailyDistance)}km",
                rank.consecutiveDays,
                "일째", "달리는 중🔥",
                cookiePick(rank.currentCookieId)
            )
        } ?: listOf()

        rankingListTier = listOf(
            RankingItem(1, "나에요", "15.34km", 18, "일째", "달리는 중🔥", R.drawable.myeongrang_cookie),
            RankingItem(2, "수아드", "12.12km", 1, "일째", "달리는 중🔥", R.drawable.zombie_cookie),
            RankingItem(3, "용쿠사기", "12.09km", 25, "일째", "달리는 중🔥", R.drawable.brave_cookie)
        )

        rankingListMe = rankingList?.userRank?.let { userRank ->
            val mainRanking = listOf(
                RankingItem(
                    userRank.rank,
                    userRank.userName,
                    "${String.format("%.2f", userRank.totalDistance)}km",
                    userRank.consecutiveDays,
                    "일째", "달리는 중🔥",
                    cookiePick(userRank.currentCookieId)
                )
            )
            // dailyRankingList의 userRank 추가
            val dailyRanking = dailyRankingList?.userRank?.let { dailyUserRank ->
                listOf(
                    RankingItem(
                        dailyUserRank.dailyRank,
                        dailyUserRank.userName,
                        "${String.format("%.2f", dailyUserRank.dailyDistance)}km",
                        dailyUserRank.consecutiveDays,
                        "일째", "오늘도 열심히! 💪",
                        cookiePick(dailyUserRank.currentCookieId)
                    )
                )
            } ?: listOf()

            // 두 리스트 합치기
            mainRanking + dailyRanking
        } ?: listOf()
    }

    private fun setupTabLayout() {
        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        val adapter = RankingFragmentAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customView = LayoutInflater.from(tabLayout.context)
                .inflate(R.layout.custom_tab, null)
            val tabText = customView.findViewById<TextView>(R.id.tab_text)
            tabText.text = when (position) {
                0 -> "전체"
                1 -> "친구"
                else -> "티어"
            }
            tab.customView = customView
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateTabContent(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateTabContent(tabPosition: Int) {
        val list = when (tabPosition) {
            0 -> rankingListAll
            1 -> rankingListDaily
            else -> rankingListTier
        }

        // 첫 번째, 두 번째, 세 번째 데이터 설정
        if (list.size >= 3) {
            binding.first.setImageResource(list[0].imageResource)
            binding.firstName.text = list[0].name
            binding.second.setImageResource(list[1].imageResource)
            binding.secondName.text = list[1].name
            binding.third.setImageResource(list[2].imageResource)
            binding.thirdName.text = list[2].name
        }

        // 현재 유저 데이터 설정
        val user = rankingListMe.getOrNull(tabPosition) ?: return
        binding.rankerName.text = user.name
        binding.rankImage.setImageResource(user.imageResource)
        binding.rank.text = user.rank.toString()
        binding.rankerDistance.text = user.distance
        binding.rankerSuccess.text = user.success.toString()
    }

    private suspend fun ranking(): AllRankingResponse? {
        return try {
            val token = requireContext()
                .getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                .getString("jwt_token", null)
            token?.let {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.ranking(it)
                }
                if (response.code() == 200) response.body() else null
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
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
}