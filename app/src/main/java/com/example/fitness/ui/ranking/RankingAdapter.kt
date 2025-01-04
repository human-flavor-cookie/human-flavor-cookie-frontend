package com.example.fitness.ui.ranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.R

class RankingAdapter(private val rankingList: List<RankingItem>) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    // ViewHolder 정의
    class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankTextView: TextView = itemView.findViewById(R.id.rank)
        val rankImageView: ImageView = itemView.findViewById(R.id.rank_image)
        val rankerNameTextView: TextView = itemView.findViewById(R.id.ranker_name)
        val rankerDistanceTextView: TextView = itemView.findViewById(R.id.ranker_distance)
        val rankerSuccessTextView: TextView = itemView.findViewById(R.id.ranker_success)
        val rankDayTextView: TextView = itemView.findViewById(R.id.ranking_days)
        val runningStatusTextView: TextView = itemView.findViewById(R.id.ranking_running)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ranking, parent, false)
        return RankingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val item = rankingList[position]

        holder.rankTextView.text = item.rank.toString()
        holder.rankerNameTextView.text = item.name
        holder.rankerDistanceTextView.text = item.distance
        holder.rankerSuccessTextView.text = item.success.toString()
        holder.rankDayTextView.text = item.day
        holder.runningStatusTextView.text = item.status
        // 이미지와 같은 추가적인 설정을 할 수도 있습니다.
        holder.rankImageView.setImageResource(item.imageResource)
    }

    override fun getItemCount(): Int = rankingList.size
}
