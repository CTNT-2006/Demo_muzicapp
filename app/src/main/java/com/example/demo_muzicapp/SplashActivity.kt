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
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        // 🔥 THÊM ĐOẠN NÀY
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

        // delay 2 giây
        handler.postDelayed(runnable, 2000)
        val title = findViewById<TextView>(R.id.title)
        title.typeface = resources.getFont(R.font.poppins_bold)


        title.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            val tv = v as TextView

            val shader = LinearGradient(
                0f, 0f, tv.width.toFloat(), tv.textSize,
                intArrayOf(
                    Color.parseColor("#FF0000"),
                    Color.parseColor("#FF7A00"),
                    Color.parseColor("#FFD700")
                ),
                null,
                Shader.TileMode.CLAMP
            )

            tv.paint.shader = shader
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

}