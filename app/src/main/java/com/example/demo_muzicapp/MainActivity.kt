package com.example.demo_muzicapp

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loading = findViewById(R.id.loading)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            showLoading()
            val fragment: Fragment = when(item.itemId) {
                R.id.nav_music -> MusicFragment()
                R.id.nav_playlist -> PlaylistFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> MusicFragment()
            }
            loadFragment(fragment)
            true
        }

        // Load mặc định
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_music
        }

        // Nếu mở trực tiếp profile từ notification / intent
        val openProfile = intent.getBooleanExtra("openProfile", false)


        if (openProfile) {
            bottomNav.selectedItemId = R.id.nav_profile
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.frameContainer, fragment)
            .commit()
        hideLoading()
    }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
        val anim = AlphaAnimation(0f, 1f)
        anim.duration = 300
        loading.startAnimation(anim)
    }

    private fun hideLoading() {
        loading.postDelayed({
            val anim = AlphaAnimation(1f, 0f)
            anim.duration = 300
            loading.startAnimation(anim)
            loading.visibility = View.GONE
        }, 500)
    }
}