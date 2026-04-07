package com.example.demo_muzicapp

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    private val runnable = Runnable {

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 🔥 LOGO ANIMATION
        val logo = findViewById<ImageView>(R.id.logo)
        logo.alpha = 0f
        logo.scaleX = 0.5f
        logo.scaleY = 0.5f

        logo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1500)
            .start()

        // 🔥 TITLE (font + gradient FIX CHUẨN)
        val title = findViewById<TextView>(R.id.title)

        // set font
        title.typeface = resources.getFont(R.font.poppins_bold)

        // 🔥 FIX: dùng post để đảm bảo đã có width → không bị lỗi không hiện màu
        title.post {
            val shader = LinearGradient(
                0f, 0f,
                title.width.toFloat(),
                title.textSize,
                intArrayOf(
                    Color.parseColor("#FF0000"),
                    Color.parseColor("#FF7A00"),
                    Color.parseColor("#FFD700")
                ),
                null,
                Shader.TileMode.CLAMP
            )
            title.paint.shader = shader
        }

        // 🔥 DELAY
        handler.postDelayed(runnable, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}