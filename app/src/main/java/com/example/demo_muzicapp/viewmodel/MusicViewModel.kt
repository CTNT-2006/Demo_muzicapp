package com.example.demo_muzicapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demo_muzicapp.data.model.Song
import com.example.demo_muzicapp.data.repository.MusicRepository
import kotlinx.coroutines.launch

class MusicViewModel(private val repository: MusicRepository) : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> = _songs

    private val _filteredSongs = MutableLiveData<List<Song>>()
    val filteredSongs: LiveData<List<Song>> = _filteredSongs

    private val _historySongs = MutableLiveData<List<Song>>()
    val historySongs: LiveData<List<Song>> = _historySongs

    private val _favoriteSongs = MutableLiveData<List<Song>>()
    val favoriteSongs: LiveData<List<Song>> = _favoriteSongs

    fun toggleFavorite(song: Song) {
        val newState = !song.isFavorite
        repository.updateFavorite(song.id, newState)
        fetchSongs() // Refresh data
        fetchFavorites()
    }

    fun fetchFavorites() {
        viewModelScope.launch {
            _favoriteSongs.value = repository.getFavoriteSongs()
        }
    }

    fun fetchSongs() {
        val allSongs = repository.getAllSongs()
        _songs.value = allSongs
        _filteredSongs.value = allSongs
    }

    fun fetchLocalSongs(context: Context) {
        val allSongs = repository.getLocalSongs(context)
        _songs.value = allSongs
        _filteredSongs.value = allSongs
    }

    fun fetchHistory() {
        _historySongs.value = repository.getHistorySongs()
    }

    fun addToHistory(songId: Int) {
        repository.insertHistory(songId)
    }

    fun filterSongs(query: String?) {
        if (query.isNullOrEmpty()) {
            _filteredSongs.value = _songs.value
        } else {
            _filteredSongs.value = _songs.value?.filter {
                it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
            }
        }
    }
}