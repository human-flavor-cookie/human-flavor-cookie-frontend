package com.example.fitness.dto.ranking

data class FriendRankingResponse (
    val top3: List<FriendRanking>,    // 상위 3명의 랭킹 정보
    val userRank: FriendRanking?,     // 현재 유저의 랭킹 정보 (없을 수도 있음)
    val allRanks: List<FriendRanking>
)
