package com.example.fitness.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.R
import com.example.fitness.dto.friend.PendingResponseDto

class NotificationAdapter(
    private val context: Context,
    private val notifications: List<NotificationItem>,
    private val onAcceptClick: (NotificationItem) -> Unit,
    private val onRejectClick: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    // 데이터 모델 클래스
    data class NotificationItem(val name: String, val message: String, val id: Long)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val messageTextView: TextView = itemView.findViewById(R.id.message)
        val profileImageView: ImageView = itemView.findViewById(R.id.imageView3)
        val acceptImageView: ImageView = itemView.findViewById(R.id.noti_accept)
        val rejectImageView: ImageView = itemView.findViewById(R.id.noti_no_accept)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]

        // 데이터 바인딩
        holder.nameTextView.text = notification.name
        holder.messageTextView.text = notification.message

        // 예시로 이미지 설정 (필요에 맞게 설정)
//        holder.profileImageView.setImageResource(R.drawable.profile_image)
//        holder.acceptImageView.setImageResource(R.drawable.accept_friend)
//        holder.rejectImageView.setImageResource(R.drawable.reject_friend)

        holder.acceptImageView.setOnClickListener{
            onAcceptClick(notification)
        }

        holder.rejectImageView.setOnClickListener {
            onRejectClick(notification)
        }

    }

    override fun getItemCount(): Int = notifications.size
}
