package com.example.demo_muzicapp.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.demo_muzicapp.data.local.DatabaseHelper
import com.example.demo_muzicapp.data.repository.MusicRepository
import com.example.demo_muzicapp.databinding.ActivityRegisterBinding
import com.example.demo_muzicapp.viewmodel.AuthViewModel
import com.example.demo_muzicapp.viewmodel.MusicViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels {
        MusicViewModelFactory(MusicRepository(DatabaseHelper(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val user = binding.edtUser.text.toString().trim()
            val pass = binding.edtPass.text.toString().trim()
            viewModel.register(user, pass)
        }
    }

    private fun setupObservers() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                AuthViewModel.AuthState.RegisterSuccess -> {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is AuthViewModel.AuthState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
}