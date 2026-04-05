package com.example.demo_muzicapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val edtUser = findViewById<EditText>(R.id.edtUser)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val user = edtUser.text.toString()
            val pass = edtPass.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                finish() // quay lại màn Welcome
            }
        }
    }
}