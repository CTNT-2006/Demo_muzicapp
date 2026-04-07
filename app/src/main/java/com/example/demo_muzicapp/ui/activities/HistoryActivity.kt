package com.example.demo_muzicapp.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo_muzicapp.data.local.DatabaseHelper
import com.example.demo_muzicapp.data.repository.MusicRepository
import com.example.demo_muzicapp.databinding.ActivityHistoryBinding
import com.example.demo_muzicapp.ui.adapters.HistoryAdapter
import com.example.demo_muzicapp.viewmodel.MusicViewModel
import com.example.demo_muzicapp.viewmodel.MusicViewModelFactory

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: MusicViewModel by viewModels {
        MusicViewModelFactory(MusicRepository(DatabaseHelper(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()

        viewModel.fetchHistory()
    }

    private fun setupRecyclerView() {
        binding.recyclerHistory.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        viewModel.historySongs.observe(this) { songs ->
            binding.recyclerHistory.adapter = HistoryAdapter(ArrayList(songs))
        }
    }
}