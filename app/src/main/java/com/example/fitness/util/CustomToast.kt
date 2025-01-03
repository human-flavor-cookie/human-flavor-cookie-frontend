package com.example.fitness.util

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.fitness.R
import com.example.fitness.databinding.ToastLayoutBinding

object CustomToast {
    fun createToast(context: Context, message: String): Toast? {
        val inflater = LayoutInflater.from(context)
        val binding: ToastLayoutBinding =
            DataBindingUtil.inflate(inflater, R.layout.toast_layout, null, false)

        binding.tvToastValue.text = message

        return Toast(context).apply {
            setGravity(Gravity.TOP, 0, 16.toPx())
            duration = Toast.LENGTH_LONG
            view = binding.root

        }
    }
}
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
