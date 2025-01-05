package com.example.fitness

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.fitness.databinding.ActivityMainBinding
import com.example.fitness.ui.cookie.CookieFragment
import com.example.fitness.ui.main.MainFragment
import com.example.fitness.ui.my.MyFragment
import com.example.fitness.ui.ranking.RankingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initBottomNavigation(savedInstanceState)
    }

    /**
     * 하단 navigation setting
     */
    private fun initBottomNavigation(savedInstanceState: Bundle?) {
        // 앱 최초 실행
        if (savedInstanceState == null) {
            binding.navArea.selectedItemId = R.id.nav_home
            openFragment(MainFragment(), Constants.MAIN)
        }

        binding.navArea.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    openFragment(MainFragment(), Constants.MAIN)
                    true
                }
                R.id.nav_cookie -> {
                    openFragment(CookieFragment(), Constants.COOKIE)
                    true
                }
                R.id.nav_ranking -> {
                    openFragment(RankingFragment(), Constants.RANKING)
                    true
                }
                R.id.nav_my -> {
                    openFragment(MyFragment(), Constants.MYPAGE)
                    true
                }
                else -> false
            }
        }
    }

    /**
     * fragment 이동
     */
    private fun openFragment(fragment: Fragment, tag: String) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    private fun isUserLoggedIn(): Boolean {
        //로그인 기능 구현
        val sharedPref = getSharedPreferences("user_pref", MODE_PRIVATE)
        return sharedPref.getBoolean("isLoggedIn", false)
    }
}