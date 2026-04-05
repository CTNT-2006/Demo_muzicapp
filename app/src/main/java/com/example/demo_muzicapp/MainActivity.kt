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
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔥 DATABASE
        dbHelper = DatabaseHelper(this)
        addSampleSong()

        // 🔥 LOADING
        loading = findViewById(R.id.loading)

        // 🔥 Gradient cho TextView
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvSongName = findViewById<TextView>(R.id.tvSongName)
        val tvArtist = findViewById<TextView>(R.id.tvArtist)

        val gradientColors = intArrayOf(
            Color.RED,
            Color.parseColor("#FFA500"), // cam
            Color.YELLOW
        )

        applyGradientText(tvTitle, gradientColors)
        applyGradientText(tvSongName, gradientColors)
        applyGradientText(tvArtist, gradientColors)

        // 🔥 BOTTOM NAV
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            showLoading()

            when (item.itemId) {
                R.id.nav_music -> loadFragment(MusicFragment())
                R.id.nav_playlist -> loadFragment(PlaylistFragment())
                R.id.nav_profile -> loadFragment(ProfileFragment())
            }
            true
        }

        if (savedInstanceState == null) {
            loadFragment(MusicFragment())
        }
    }

    // Hàm áp dụng gradient chữ cho TextView
    private fun applyGradientText(textView: TextView, colors: IntArray) {
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())
        val shader = LinearGradient(
            0f, 0f, width, textView.textSize,
            colors,
            null,
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = shader
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

    private fun addSampleSong() {
        val list = dbHelper.getAllSongs()

        if (list.isEmpty()) {

            dbHelper.insertSong(
                Song(
                    0,
                    "See You Again",
                    "Wiz Khalifa ft. Charlie Puth",
                    "",
                    "",
                    "android.resource://$packageName/${R.raw.song1}"
                )
            )

            dbHelper.insertSong(
                Song(
                    0,
                    "Faded",
                    "Alan Walker",
                    "",
                    "",
                    "android.resource://$packageName/${R.raw.song2}"
                )
            )

            dbHelper.insertSong(
                Song(
                    0,
                    "Believer",
                    "Imagine Dragons",
                    "",
                    "",
                    "android.resource://$packageName/${R.raw.song3}"
                )
            )
        }
    }
}