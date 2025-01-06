package com.example.fitness.dto.ranking

data class AllRankingResponse (
    val top3: List<RankingItem>,    // 상위 3명의 랭킹 정보
    val userRank: RankingItem?,     // 현재 유저의 랭킹 정보 (없을 수도 있음)
    val allRanks: List<RankingItem>
)
