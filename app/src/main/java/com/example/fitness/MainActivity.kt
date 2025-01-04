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
import com.example.fitness.ui.main.MainFragment
import com.example.fitness.ui.ranking.RankingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그인 상태 확인
        if (!isUserLoggedIn()) {
            // 로그인되지 않은 경우 LoginActivity로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // MainActivity 종료
            return
        }

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
                R.id.nav_board -> {
                    //openFragment(BoardFragment(), HwConstants.BOARD)
                    true
                }
                R.id.nav_mypage -> {
                    openFragment(RankingFragment(), Constants.MYPAGE)
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