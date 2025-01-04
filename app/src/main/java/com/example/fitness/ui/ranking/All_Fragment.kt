package com.example.fitness.ui.ranking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.R

class All_Fragment : Fragment(R.layout.fragment_all) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context) // 수직 스크롤

        // 데이터 설정
        val rankingList = listOf(
            RankingItem(1, "나에요", "15.34km", 18, "일째", "달리는 중🔥", R.drawable.zombie_cookie),
            RankingItem(2, "용쿠사기", "13.67km", 2, "일째", "달리는 중🔥", R.drawable.brave_cookie),
            RankingItem(3, "10km미만잡", "10.09km", 146, "일째", "달리는 중🔥", R.drawable.myeongrang_cookie),
            RankingItem(2, "용쿠사기", "13.67km", 2, "일째", "달리는 중🔥", R.drawable.brave_cookie),
            RankingItem(3, "10km미만잡", "10.09km", 146, "일째", "달리는 중🔥", R.drawable.myeongrang_cookie)
        )

        // 어댑터 설정
        val adapter = RankingAdapter(rankingList)
        recyclerView.adapter = adapter
    }

//    // 데이터 목록 (예시로 간단한 텍스트 리스트)
//    private fun getData(): List<String> {
//        return listOf("Tab 2 - Item 1", "Tab 2 - Item 2", "Tab 2 - Item 3")
//    }

}