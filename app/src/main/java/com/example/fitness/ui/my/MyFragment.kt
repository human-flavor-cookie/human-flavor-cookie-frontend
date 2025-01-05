package com.example.fitness.ui.my

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.fitness.LoginActivity
import com.example.fitness.MainActivity
import com.example.fitness.R
import com.example.fitness.databinding.FragmentMyBinding

class MyFragment : Fragment(R.layout.fragment_my) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트 레이아웃 Inflate
        val view = inflater.inflate(R.layout.fragment_my, container, false)

        // 버튼 참조
        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            showPopup()
        }

        return view
    }

    private fun showPopup() {
        // 팝업 레이아웃 Inflate
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.logout_layout, null)

        // AlertDialog 생성
        val dialog = AlertDialog.Builder(requireContext())

            .setView(popupView)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 팝업 내 닫기 버튼 이벤트 설정
        val closeButton = popupView.findViewById<ImageButton>(R.id.cancel_button)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        val goButton = popupView.findViewById<ImageButton>(R.id.go_button)
        goButton.setOnClickListener {
            // 메인 화면으로 이동
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish() // 현재 액티비티 종료
        }
        // 팝업 표시
        dialog.show()
    }
}