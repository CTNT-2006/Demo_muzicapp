package com.example.demo_muzicapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.demo_muzicapp.data.local.DatabaseHelper
import com.example.demo_muzicapp.data.repository.MusicRepository
import com.example.demo_muzicapp.databinding.FragmentProfileBinding
import com.example.demo_muzicapp.ui.activities.LoginActivity
import com.example.demo_muzicapp.ui.activities.RegisterActivity
import com.example.demo_muzicapp.viewmodel.AuthViewModel
import com.example.demo_muzicapp.viewmodel.MusicViewModelFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        MusicViewModelFactory(MusicRepository(DatabaseHelper(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        updateUI()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            clearUser()
            updateUI()
        }
    }

    private fun clearUser() {
        val prefs = requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    private fun updateUI() {
        val prefs = requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE)
        val isLogin = prefs.getBoolean("isLoggedIn", false)

        if (isLogin) {
            binding.layoutGuest.visibility = View.GONE
            binding.layoutProfileLogged.visibility = View.VISIBLE

            val username = prefs.getString("username", "User")
            val role = prefs.getString("role", "User")

            binding.txtName.text = username
            binding.txtRole.text = role

            setupPlaylistRecyclerView()
        } else {
            binding.layoutGuest.visibility = View.VISIBLE
            binding.layoutProfileLogged.visibility = View.GONE
        }
    }

    private fun setupPlaylistRecyclerView() {
        // Implement playlist adapter and data fetching here
        // binding.rvUserPlaylists.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}