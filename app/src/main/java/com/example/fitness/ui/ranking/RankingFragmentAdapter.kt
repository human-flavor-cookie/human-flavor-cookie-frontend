package com.example.fitness.ui.ranking

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class RankingFragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    // 탭의 개수 (3개의 탭)
    override fun getItemCount(): Int = 3

    // 각 탭에 해당하는 Fragment 반환
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> All_Fragment()  // 첫 번째 탭
            1 -> Friend_Fragment()  // 두 번째 탭
            else -> Tier_Fragment()  // 세 번째 탭
        }
    }
}