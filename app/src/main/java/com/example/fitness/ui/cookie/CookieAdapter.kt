package com.example.fitness.ui.cookie

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.fitness.R
import com.example.fitness.api.RetrofitClient
import com.example.fitness.dto.cookie.CookieChangeRequestDto
import com.example.fitness.dto.cookie.CookieListResponse
import com.example.fitness.dto.running.RunningRequest
import com.example.fitness.ui.ranking.RankingAdapter
import com.example.fitness.ui.ranking.RankingItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CookieAdapter(
    private val context: Context,
    private val cookieList: MutableList<CookieItem>) :
    RecyclerView.Adapter<CookieAdapter.CookieViewHolder>() {

    // user 누적 거리
    private val userDistance = 20f

    // ViewHolder 정의
    class CookieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cookieName: TextView = itemView.findViewById(R.id.cookie_name)
        val properties: TextView = itemView.findViewById(R.id.properties)
        val distanceWith: TextView = itemView.findViewById(R.id.distance_with)
        val distanceWithInt: TextView = itemView.findViewById(R.id.distance_with_int)
        val cookieImage: ImageView = itemView.findViewById(R.id.cookie_image)
        val selectButton: ImageView = itemView.findViewById(R.id.select_button)
        val cookiePriceOrCondition: TextView = itemView.findViewById(R.id.condition_or_price)
        val itemLayout: View = itemView // 아이템 전체
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cookie, parent, false)
        return CookieViewHolder(view)
    }

    override fun onBindViewHolder(holder: CookieViewHolder, position: Int) {
        val cookie = cookieList[position]

        // 데이터 설정
        holder.cookieName.text = cookie.name
        holder.properties.text = cookie.properties
        holder.distanceWith.text = cookie.distanceWith
        holder.distanceWithInt.text = cookie.distanceWithInt
        holder.cookieImage.setImageResource(cookie.imageRes)
        //holder.cookiePriceOrCondition.text = cookie.priceOrConditon
        // isDisabled 조건에 따라 select_button 이미지 변경
        // 깨진경우..해금 됐지만..
        if (cookie.purchasable) {
            holder.selectButton.setImageResource(R.drawable.cookie_price_background) // 비활성화 이미지
            holder.cookiePriceOrCondition.text = cookie.priceOrConditon
            //깨진 경우
            if(!cookie.alive){
                if (cookie.name == "명랑한맛 쿠키") {
                    holder.cookieImage.setImageResource(R.drawable.happy_cookie_dead)
                } else if (cookie.name == "버터크림맛 쿠키") {
                    holder.cookieImage.setImageResource(R.drawable.buttecookie_die)
                } else if (cookie.name == "천사맛 쿠키") {
                    holder.cookieImage.setImageResource(R.drawable.angel_die)
                }
            }
        }
        //해금 안된 경우
        else if (!cookie.owned && !cookie.purchasable){
            holder.selectButton.setImageResource(R.drawable.select_button_cookie_lock) // 잠금
            if (cookie.name == "좀비맛 쿠키") {
                holder.cookiePriceOrCondition.text = "1시간 동안 달리기"
            } else if (cookie.name == "천사맛 쿠키") {
                holder.cookiePriceOrCondition.text = "7일 연속 성공"
            } else if (cookie.name == "버터크림맛 쿠키") {
                holder.cookiePriceOrCondition.text = "누적 10km 달리기"
            } else if (cookie.name == "용감한맛 쿠키") {
                holder.cookiePriceOrCondition.text = ""
            } else if (cookie.name == "명랑한맛 쿠키") {
                holder.cookiePriceOrCondition.text = ""
            }

//            applyGrayscale(holder)
        }
        //해금 됐고.. 쿠키 안부셨는데.. 선택 안한 경우
        else if (cookie.owned && !cookie.isSelected){
            holder.selectButton.setImageResource(R.drawable.select_cookie) // 해금
        }

        // 특정 조건에서 흑백 처리 - 해금 안된 경우 + 깨진 경우
        if ((!cookie.owned && !cookie.purchasable) || cookie.purchasable) { // 조건: isDisabled가 true인 경우
            applyGrayscale(holder)
        } else {
            clearGrayscale(holder)
        }

        // selectButton 클릭 리스너 - 해금됐고 쿠키 안부셔졌는데 선택 안한 경우
        holder.selectButton.setOnClickListener {
            if (cookie.owned && !cookie.isSelected) {
                // 모든 쿠키 항목의 isSelected 값을 false로 설정
                cookieList.forEachIndexed { index, cookieItem ->
                    // 각 항목의 isSelected를 false로 설정
                    cookieList[index] = cookieItem.copy(isSelected = false)
                }
                cookieList[position] = cookie.copy(isSelected = true)
                cookieChange(CookieChangeRequestDto((position + 1).toLong()))
                // 어댑터 갱신
                notifyDataSetChanged()
            }

            if (cookie.purchasable) {
                cookiePurchase(
                    CookieChangeRequestDto((position + 1).toLong()),
                    onSuccess = {
                        val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                        fragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment, CookieFragment()) // R.id.fragment_container는 프래그먼트를 담는 컨테이너 ID
                            .commit()
                    },
                    onFailure = {
                        Log.d("cookiePurchase", "쿠키 구매 실패")
                    }
                )
            }
        }
        if (cookie.isSelected) {
            holder.selectButton.setImageResource(R.drawable.cookie_using)
        }
    }

    override fun getItemCount(): Int {
        return cookieList.size
    }

    // 흑백 처리 함수
    private fun applyGrayscale(holder: CookieViewHolder) {
        // 이미지에 흑백 필터 적용
        val matrix = ColorMatrix().apply { setSaturation(0f) }
        val filter = ColorMatrixColorFilter(matrix)
        holder.cookieImage.colorFilter = filter

        // 텍스트 색상을 회색으로 변경
        holder.cookieName.setTextColor(Color.GRAY)
        holder.properties.setTextColor(Color.GRAY)
        holder.distanceWithInt.setTextColor(Color.GRAY)


//        // 전체 배경을 흐리게 (선택적)
//        holder.itemLayout.alpha = 0.5f
    }

    // 흑백 필터 제거
    private fun clearGrayscale(holder: CookieViewHolder) {
        // 이미지 필터 제거
        holder.cookieImage.colorFilter = null

        // 텍스트 색상을 기본값으로 설정
        holder.cookieName.setTextColor(Color.BLACK)
        holder.properties.setTextColor(Color.BLACK)
        holder.distanceWithInt.setTextColor(Color.BLACK)

        // 전체 배경 투명도 복원 (선택적)
        holder.itemLayout.alpha = 1.0f
    }

    private fun cookieChange(request: CookieChangeRequestDto) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    .getString("jwt_token", null)
                if (token != null) {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.instance.cookieChange(token, request)
                    }
                    if (response.code() == 200) {
                        Log.d("cookieChange", "쿠키 변경 성공")
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cookiePurchase(request: CookieChangeRequestDto, onSuccess: () -> Unit, onFailure: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                Log.d("cookiePurchase", "쿠키 구매 시작")
                val token = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
                    .getString("jwt_token", null)
                if (token != null) {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.instance.cookiePurchase(token, request)
                    }
                    Log.d("cookiePurchase", "쿠키 토큰 성공")
                    if (response.code() == 200) {
                        Log.d("cookiePurchase", "쿠키 구매 성공")
                        onSuccess()
                    } else if (response.code() == 400) {
                        Toast.makeText(context, "쿠키를 살 수 있는 돈이 부족합니다.", Toast.LENGTH_SHORT).show()
                        onFailure()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                onFailure()
            }
        }
    }
}
