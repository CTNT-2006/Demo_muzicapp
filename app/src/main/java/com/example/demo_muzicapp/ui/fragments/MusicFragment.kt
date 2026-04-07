package com.example.demo_muzicapp.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo_muzicapp.data.local.DatabaseHelper
import com.example.demo_muzicapp.data.repository.MusicRepository
import com.example.demo_muzicapp.databinding.FragmentMusicBinding
import com.example.demo_muzicapp.ui.adapters.SongAdapter
import com.example.demo_muzicapp.viewmodel.MusicViewModel
import com.example.demo_muzicapp.viewmodel.MusicViewModelFactory
import com.example.demo_muzicapp.viewmodel.PlayerViewModel

class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MusicViewModel by viewModels {
        MusicViewModelFactory(MusicRepository(DatabaseHelper(requireContext())))
    }

    private val playerViewModel: PlayerViewModel by activityViewModels()

    private lateinit var adapter: SongAdapter

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.fetchLocalSongs(requireContext())
        } else {
            Toast.makeText(requireContext(), "Permission denied to read music", Toast.LENGTH_SHORT).show()
            viewModel.fetchSongs() // Fallback to DB songs
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupSearchView()

        checkPermissionsAndFetchSongs()
    }

    private fun checkPermissionsAndFetchSongs() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.fetchLocalSongs(requireContext())
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = SongAdapter(emptyList()) { clickedSong ->
            viewModel.addToHistory(clickedSong.id)
            
            val currentList = viewModel.filteredSongs.value ?: emptyList()
            val index = currentList.indexOf(clickedSong)
            
            playerViewModel.playSongs(currentList, index)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.filteredSongs.observe(viewLifecycleOwner) { songs ->
            adapter.updateList(songs)
        }
    }

    private fun setupSearchView() {
        binding.searchViewMusic.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterSongs(newText)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
