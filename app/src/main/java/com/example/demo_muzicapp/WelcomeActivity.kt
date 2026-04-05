package com.example.demo_muzicapp

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)


        val title = findViewById<TextView>(R.id.title)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGuest = findViewById<Button>(R.id.btnGuest)


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

        // ===== LOGIN =====
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // ===== REGISTER =====
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // ===== GUEST =====
        btnGuest.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}