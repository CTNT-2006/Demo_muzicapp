package com.example.demo_muzicapp.ui.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.demo_muzicapp.data.local.DatabaseHelper
import com.example.demo_muzicapp.data.repository.MusicRepository
import com.example.demo_muzicapp.databinding.ActivityLoginBinding
import com.example.demo_muzicapp.databinding.DialogRegisterBinding
import com.example.demo_muzicapp.viewmodel.AuthViewModel
import com.example.demo_muzicapp.viewmodel.MusicViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels {
        MusicViewModelFactory(MusicRepository(DatabaseHelper(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.edtUser.text.toString().trim()
            val password = binding.edtPass.text.toString().trim()
            viewModel.login(username, password)
        }

        binding.tvRegister.setOnClickListener {
            showRegisterDialog()
        }
    }

    private fun setupObservers() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthViewModel.AuthState.Success -> {
                    saveLoginState(state.username, state.role)
                    Toast.makeText(this, "Đăng nhập thành công 🎉", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is AuthViewModel.AuthState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                AuthViewModel.AuthState.RegisterSuccess -> {
                    Toast.makeText(this, "Đăng ký thành công ✔", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showRegisterDialog() {
        val dialogBinding = DialogRegisterBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnRegister.setOnClickListener {
            val user = dialogBinding.edtUser.text.toString().trim()
            val pass = dialogBinding.edtPass.text.toString().trim()
            viewModel.register(user, pass)
            // Note: In a real MVVM, the dialog might observe a separate LiveData or state
            // For simplicity here, we assume register success or error is handled by the common observer
            // but we might need to dismiss the dialog on success.
        }
        
        // Let's refine the observer to handle dialog dismissal if needed, 
        // or just rely on Toast for now.
        dialog.show()
        
        // Simple way to handle dialog dismissal upon success in this scope
        viewModel.authState.observe(this) { state ->
            if (state is AuthViewModel.AuthState.RegisterSuccess) {
                dialog.dismiss()
            }
        }
    }

    private fun saveLoginState(username: String, role: String) {
        val prefs = getSharedPreferences("USER", MODE_PRIVATE)
        prefs.edit()
            .putBoolean("isLoggedIn", true)
            .putString("username", username)
            .putString("role", role)
            .apply()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("openProfile", true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}