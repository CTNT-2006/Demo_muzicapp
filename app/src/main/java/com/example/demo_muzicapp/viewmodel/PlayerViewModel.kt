package com.example.demo_muzicapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.demo_muzicapp.data.model.Song

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private var exoPlayer: ExoPlayer? = null
    
    private val _currentSong = MutableLiveData<Song?>()
    val currentSong: LiveData<Song?> = _currentSong

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _progress = MutableLiveData<Long>(0L)
    val progress: LiveData<Long> = _progress

    private val _duration = MutableLiveData<Long>(0L)
    val duration: LiveData<Long> = _duration

    private val _isRepeatOne = MutableLiveData<Boolean>(false)
    val isRepeatOne: LiveData<Boolean> = _isRepeatOne

    private val _isShuffle = MutableLiveData<Boolean>(false)
    val isShuffle: LiveData<Boolean> = _isShuffle

    private val _isFavorite = MutableLiveData<Boolean>(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val databaseHelper = com.example.demo_muzicapp.data.local.DatabaseHelper(application)
    private var songList: List<Song> = emptyList()
    private var currentIndex: Int = -1

    init {
        setupPlayer()
    }

    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(getApplication()).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val index = exoPlayer?.currentMediaItemIndex ?: -1
                    if (index >= 0 && index < songList.size) {
                        currentIndex = index
                        val song = songList[currentIndex]
                        _currentSong.value = song
                        _isFavorite.value = song.isFavorite
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = duration
                    }
                }
            })
        }
    }

    fun playSongs(songs: List<Song>, index: Int) {
        this.songList = songs
        this.currentIndex = index
        
        exoPlayer?.let { player ->
            player.stop()
            player.clearMediaItems()
            songs.forEach { song ->
                player.addMediaItem(MediaItem.fromUri(Uri.parse(song.file)))
            }
            player.seekTo(index, 0)
            player.prepare()
            player.play()
        }
        _currentSong.value = songs[index]
        _isFavorite.value = songs[index].isFavorite
    }

    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun toggleRepeat() {
        _isRepeatOne.value = !(_isRepeatOne.value ?: false)
        exoPlayer?.repeatMode = if (_isRepeatOne.value == true) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
    }

    fun toggleShuffle() {
        _isShuffle.value = !(_isShuffle.value ?: false)
        exoPlayer?.shuffleModeEnabled = _isShuffle.value ?: false
    }

    fun toggleFavorite() {
        val song = _currentSong.value ?: return
        val newFavoriteStatus = !(_isFavorite.value ?: false)
        databaseHelper.updateFavorite(song.id, newFavoriteStatus)
        _isFavorite.value = newFavoriteStatus
        song.isFavorite = newFavoriteStatus
    }

    fun next() {
        exoPlayer?.seekToNextMediaItem()
    }

    fun previous() {
        exoPlayer?.seekToPreviousMediaItem()
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    fun updateProgress() {
        _progress.value = exoPlayer?.currentPosition ?: 0L
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }

    fun getExoPlayer(): ExoPlayer? = exoPlayer
}
