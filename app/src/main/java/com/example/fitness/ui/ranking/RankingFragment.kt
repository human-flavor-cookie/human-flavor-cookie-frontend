package com.example.fitness.ui.ranking

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.fitness.R
import com.example.fitness.api.RetrofitClient
import com.example.fitness.databinding.FragmentRankingBinding
import com.example.fitness.dto.cookie.CookieListResponse
import com.example.fitness.dto.ranking.AllRankingResponse
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class RankingFragment : Fragment(R.layout.fragment_ranking) {

    private lateinit var binding: FragmentRankingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRankingBinding.bind(view)
        var rankingList: AllRankingResponse? = null

        lifecycleScope.launch {
            rankingList = ranking()

            // 데이터 설정
            val rankingList_all = rankingList?.allRanks?.mapIndexed { _, rank ->
                RankingItem(
                    rank.rank,
                    rank.userName,
                    "${rank.totalDistance}km",
                    rank.consecutiveDays,
                    "일째", "달리는 중🔥",
                    if (rank.userName == "나에요") R.drawable.zombie_cookie else R.drawable.brave_cookie
                )
            } ?: listOf()// 데이터 설정
            val rankingList_friend = listOf(
                RankingItem(1, "안녕하세요", "15.34km", 18, "일째", "달리는 중🔥", R.drawable.brave_cookie),
                RankingItem(2, "용쿠사기", "12.12km", 20, "일째", "달리는 중🔥", R.drawable.zombie_cookie),
                RankingItem(3, "용쿠사기2", "4.05km", 25, "일째", "달리는 중🔥", R.drawable.brave_cookie)
                // 더 많은 데이터 추가 가능
            )

            // 데이터 설정
            val rankingList_tier = listOf(
                RankingItem(1, "나에요", "15.34km", 18, "일째", "달리는 중🔥", R.drawable.myeongrang_cookie),
                RankingItem(2, "수아드", "12.12km", 1, "일째", "달리는 중🔥", R.drawable.zombie_cookie),
                RankingItem(3, "용쿠사기", "12.09km", 25, "일째", "달리는 중🔥", R.drawable.brave_cookie)
                // 더 많은 데이터 추가 가능
            )

            // 데이터 설정
            val rankingList_me = listOf(
                RankingItem(123, "주찬", "15.34km", 18, "일째", "달리는 중🔥", R.drawable.myeongrang_cookie),
                RankingItem(4, "주찬", "15.34km", 18, "일째", "달리는 중🔥", R.drawable.myeongrang_cookie),
                RankingItem(1, "주찬", "15.34km", 18, "일째", "달리는 중🔥", R.drawable.myeongrang_cookie)
                // 더 많은 데이터 추가 가능
            )


            val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
            val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

            // Adapter 설정
            val adapter = RankingFragmentAdapter(this@RankingFragment)
            viewPager.adapter = adapter

            // TabLayout과 ViewPager2 연결
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                // Inflate 커스텀 뷰
                val customView = LayoutInflater.from(tabLayout.context)
                    .inflate(R.layout.custom_tab, null)
                val tabText = customView.findViewById<TextView>(R.id.tab_text)
                tabText.text = when (position) {
                    0 -> "전체"  // 첫 번째 탭
                    1 -> "친구"  // 두 번째 탭
                    else -> "티어"  // 세 번째 탭
                }

                // 탭에 커스텀 뷰 적용
                tab.customView = customView
            }.attach()

            // "전체" 탭에 해당하는 초기 데이터 설정
            val firstItem = rankingList_me[0] // 첫 번째 데이터 사용

            // FrameLayout 내 요소들 초기값 설정
            binding.rank.text = firstItem.rank.toString()
            binding.rankImage.setImageResource(firstItem.imageResource)
            binding.rankerName.text = firstItem.name
            binding.rankerDistance.text = firstItem.distance
            binding.rankerSuccess.text = firstItem.success.toString()

            // TabLayout 선택 이벤트
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
//                tab?.view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.cookie_brown))
                    if (tab?.position == 0) { // "전체" 탭이 선택된 경우
                        val firstItem = rankingList_all[0] // 첫 번째 데이터를 사용
                        binding.first.setImageResource(firstItem.imageResource)
                        binding.firstName.text = firstItem.name
                        val secondItem = rankingList_all[1]
                        binding.second.setImageResource(secondItem.imageResource)
                        binding.secondName.text = secondItem.name
                        val thirdItem = rankingList_all[2]
                        binding.third.setImageResource(thirdItem.imageResource)
                        binding.thirdName.text = thirdItem.name
                        val user = rankingList_me[0]
                        binding.rankerName.text = user.name
                        binding.rankImage.setImageResource(user.imageResource)
                        binding.rank.text = user.rank.toString()
                        binding.rankerDistance.text = user.distance
                        binding.rankerSuccess.text = user.success.toString()
                    }
                    else if (tab?.position == 1) {
                        val firstItem = rankingList_friend[0] // 첫 번째 데이터를 사용
                        binding.first.setImageResource(firstItem.imageResource)
                        binding.firstName.text = firstItem.name
                        val secondItem = rankingList_friend[1]
                        binding.second.setImageResource(secondItem.imageResource)
                        binding.secondName.text = secondItem.name
                        val thirdItem = rankingList_friend[2]
                        binding.third.setImageResource(thirdItem.imageResource)
                        binding.thirdName.text = thirdItem.name
                        val user = rankingList_me[1]
                        binding.rankerName.text = user.name
                        binding.rankImage.setImageResource(user.imageResource)
                        binding.rank.text = user.rank.toString()
                        binding.rankerDistance.text = user.distance
                        binding.rankerSuccess.text = user.success.toString()
                    }
                    else {
                        val firstItem = rankingList_tier[0] // 첫 번째 데이터를 사용
                        binding.first.setImageResource(firstItem.imageResource)
                        binding.firstName.text = firstItem.name
                        val secondItem = rankingList_tier[1]
                        binding.second.setImageResource(secondItem.imageResource)
                        binding.secondName.text = secondItem.name
                        val thirdItem = rankingList_tier[2]
                        binding.third.setImageResource(thirdItem.imageResource)
                        binding.thirdName.text = thirdItem.name
                        val user = rankingList_me[2]
                        binding.rankerName.text = user.name
                        binding.rankImage.setImageResource(user.imageResource)
                        binding.rank.text = user.rank.toString()
                        binding.rankerDistance.text = user.distance
                        binding.rankerSuccess.text = user.success.toString()
                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
//                // 비선택된 탭의 배경색 초기화
//                tab?.view?.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    // 이미 선택된 탭 다시 선택 시 필요한 작업
                }
            })

        }

    }

    private suspend fun ranking(): AllRankingResponse? {
        var rankingList: AllRankingResponse ?= null
        try {
            val token = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE).getString("jwt_token", null)
            if (token != null) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.ranking(token)
                }

                if (response.code() == 200) {
                    rankingList = response.body()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        return rankingList
    }

    private fun cookiePick(cookieId: Long): Int{
        if(cookieId.toInt() == 1) return R.drawable.brave_cookie
        if(cookieId.toInt() == 2) return R.drawable.brave_cookie
        if(cookieId.toInt() == 3) return R.drawable.brave_cookie
        if(cookieId.toInt() == 4) return R.drawable.brave_cookie
        if(cookieId.toInt() == 5) return R.drawable.brave_cookie
    }
}
