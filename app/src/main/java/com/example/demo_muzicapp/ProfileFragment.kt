package com.example.demo_muzicapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    // Layout
    private lateinit var layoutGuest: View
    private lateinit var layoutLogged: View

    // Guest buttons
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnGuest: Button

    // Logged UI
    private lateinit var txtName: TextView
    private lateinit var txtRole: TextView
    private lateinit var btnLogout: LinearLayout   // ✅ FIX ở đây
    private lateinit var btnPremium: LinearLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔥 Layouts
        layoutGuest = view.findViewById(R.id.layoutGuest)
        layoutLogged = view.findViewById(R.id.layout_profile_logged)

        // 🔥 Guest buttons
        btnLogin = view.findViewById(R.id.btnLogin)
        btnRegister = view.findViewById(R.id.btnRegister)
        btnGuest = view.findViewById(R.id.btnGuest)

        // 🔥 Logged UI
        txtName = view.findViewById(R.id.txtName)
        txtRole = view.findViewById(R.id.txtRole)
        btnLogout = view.findViewById(R.id.btnLogout)   // ✅ không còn ImageButton
        btnPremium = view.findViewById(R.id.btnPremium)

        // ===== CLICK EVENTS =====

        btnLogin.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
        }

        btnGuest.setOnClickListener {
            saveUser("Guest", "Guest")
            updateUI()
        }

        btnLogout.setOnClickListener {
            clearUser()
            updateUI()
        }

        btnPremium.setOnClickListener {
            // TODO: mở Premium sau
        }

        updateUI()
    }

    private fun saveUser(username: String, role: String) {
        val prefs = requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("isLoggedIn", true)
            .putString("username", username)
            .putString("role", role)
            .apply()
    }

    private fun clearUser() {
        val prefs = requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    private fun updateUI() {
        val prefs = requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE)
        val isLogin = prefs.getBoolean("isLoggedIn", false)

        if (isLogin) {
            layoutGuest.visibility = View.GONE
            layoutLogged.visibility = View.VISIBLE

            val username = prefs.getString("username", "User")
            val role = prefs.getString("role", "User")

            txtName.text = username
            txtRole.text = role
        } else {
            layoutGuest.visibility = View.VISIBLE
            layoutLogged.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }
}