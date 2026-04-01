package com.example.demo_muzicapp
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = DatabaseHelper(this)

        val user = findViewById<EditText>(R.id.edtUser)
        val pass = findViewById<EditText>(R.id.edtPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val tvRegister = findViewById<TextView>(R.id.tvRegister)

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
                val user = edtUserReg.text.toString()
                val pass = edtPassReg.text.toString()

                val success = db.register(user, pass)

                if (success) {
                    Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "User đã tồn tại", Toast.LENGTH_SHORT).show()
                }
            }
        }


        btnLogin.setOnClickListener {
            val username = user.text.toString().trim()
            val password = pass.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            val role = db.login(username, password)

            if (role != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("username", username)
                intent.putExtra("role", role)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }
    }
}