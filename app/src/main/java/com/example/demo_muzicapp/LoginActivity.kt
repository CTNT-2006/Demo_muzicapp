package com.example.demo_muzicapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        val edtUser = findViewById<EditText>(R.id.edtUser)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        // ===== REGISTER =====
        tvRegister.setOnClickListener {

            val dialogView = layoutInflater.inflate(R.layout.dialog_register, null)

            val edtUserReg = dialogView.findViewById<EditText>(R.id.edtUserReg)
            val edtPassReg = dialogView.findViewById<EditText>(R.id.edtPassReg)
            val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmitReg)

            val dialog = android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            dialog.show()

            btnSubmit.setOnClickListener {
                val user = edtUserReg.text.toString().trim()
                val pass = edtPassReg.text.toString().trim()

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (pass.length < 4) {
                    Toast.makeText(this, "Mật khẩu tối thiểu 4 ký tự", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val success = db.register(user, pass)

                if (success) {
                    Toast.makeText(this, "Đăng ký thành công ✔", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "User đã tồn tại ❌", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ===== LOGIN =====
        btnLogin.setOnClickListener {

            val username = edtUser.text.toString().trim()
            val password = edtPass.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val role = db.login(username, password)

            if (role != null) {

                // 🔥 LƯU LOGIN
                val prefs = getSharedPreferences("USER", MODE_PRIVATE)
                prefs.edit()
                    .putBoolean("isLoggedIn", true)
                    .putString("username", username)
                    .putString("role", role)
                    .commit() // 🔥 dùng commit cho chắc

                // 🔥 DEBUG
                val check = prefs.getBoolean("isLoggedIn", false)
                println("LOGIN SAVED = $check")

                Toast.makeText(this, "Đăng nhập thành công 🎉", Toast.LENGTH_SHORT).show()

                // 🔥 ÉP MAIN ACTIVITY RELOAD PROFILE
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("openProfile", true)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)



            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }
}