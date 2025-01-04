package com.example.fitness.ui.ranking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.fitness.R
import com.example.fitness.databinding.FragmentRankingBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankingFragment : Fragment(R.layout.fragment_ranking) {

    private lateinit var binding: FragmentRankingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRankingBinding.bind(view)

        // 데이터 설정
        val rankingList_all = listOf(
            RankingItem(1, "나에요", "15.34km", 18, "일째", "달리는 중🔥", R.drawable.zombie_cookie),
            RankingItem(2, "용쿠사기", "13.67km", 2, "일째", "달리는 중🔥", R.drawable.brave_cookie),
            RankingItem(3, "10km미만잡", "10.09km", 146, "일째", "달리는 중🔥", R.drawable.myeongrang_cookie)
            // 더 많은 데이터 추가 가능
        )// 데이터 설정
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

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        // Adapter 설정
        val adapter = RankingFragmentAdapter(this)
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
