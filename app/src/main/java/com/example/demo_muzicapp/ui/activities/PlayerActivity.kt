package com.example.demo_muzicapp.ui.activities

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.demo_muzicapp.R
import com.example.demo_muzicapp.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private var exoPlayer: ExoPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var songList = ArrayList<String>()
    private var currentIndex = 0
    private var rotateSong: ObjectAnimator? = null
    private var isRepeatOne = false
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ===== NHẬN DATA =====
        val list = intent.getStringArrayListExtra("list")
        currentIndex = intent.getIntExtra("index", 0)

        if (!list.isNullOrEmpty()) {
            songList = list
        } else {
            intent.getStringExtra("file")?.let { songList.add(it) }
        }

        if (songList.isEmpty()) {
            finish()
            return
        }

        setupExoPlayer()

        // ===== ANIMATION =====
        rotateSong = ObjectAnimator.ofFloat(binding.cardDisc, "rotation", 0f, 360f).apply {
            duration = 15000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }

        // ===== LISTENERS =====
        binding.btnPlay.setOnClickListener {
            exoPlayer?.let {
                if (it.isPlaying) it.pause() else it.play()
            }
        }

        binding.btnNext.setOnClickListener { nextSong() }
        binding.btnPrevious.setOnClickListener { prevSong() }
        binding.btnBack.setOnClickListener { finish() }

        binding.btnFavorite.setOnClickListener {
            binding.btnFavorite.setImageResource(
                if (binding.btnFavorite.tag == "liked") {
                    binding.btnFavorite.tag = "unliked"
                    android.R.drawable.btn_star_big_off
                } else {
                    binding.btnFavorite.tag = "liked"
                    android.R.drawable.btn_star_big_on
                }
            )
            Toast.makeText(this, "Đã cập nhật yêu thích", Toast.LENGTH_SHORT).show()
        }

        binding.btnShuffle.setOnClickListener {
            Toast.makeText(this, "Tính năng Shuffle đang phát triển", Toast.LENGTH_SHORT).show()
        }

        binding.waveformView.setOnProgressChangedListener(object : com.example.demo_muzicapp.ui.views.WaveformView.OnProgressChangedListener {
            override fun onProgressChanged(progress: Float, fromUser: Boolean) {
                if (fromUser) {
                    exoPlayer?.let {
                        val seekPosition = (progress * it.duration).toLong()
                        it.seekTo(seekPosition)
                    }
                }
            }
        })

        setupSwipeGestures()
    }

    private fun setupSwipeGestures() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Vuốt sang phải -> Bài trước
                        prevSong()
                        Toast.makeText(this@PlayerActivity, "Bài trước", Toast.LENGTH_SHORT).show()
                    } else {
                        // Vuốt sang trái -> Bài tiếp theo
                        nextSong()
                        Toast.makeText(this@PlayerActivity, "Bài tiếp theo", Toast.LENGTH_SHORT).show()
                    }
                    return true
                }
                return false
            }
        })

        // Gán listener cho root view của player
        binding.root.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun setupExoPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            songList.forEach { path ->
                addMediaItem(MediaItem.fromUri(Uri.parse(path)))
            }
            seekTo(currentIndex, 0)
            prepare()
            playWhenReady = true

            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        binding.btnPlay.setImageResource(android.R.drawable.ic_media_pause)
                        startAnimation()
                    } else {
                        binding.btnPlay.setImageResource(android.R.drawable.ic_media_play)
                        pauseAnimation()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    currentIndex = currentMediaItemIndex
                    val path = songList[currentIndex]
                    updateUI(path)
                    binding.waveformView.setSeed(path.hashCode().toLong())
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        binding.txtTotalTime.text = formatTime(duration.toInt())
                        handler.post(updateProgress)
                    }
                }
            })
        }
    }

    private fun updateUI(path: String) {
        binding.songTitle.text = getFileName(path)
        
        // Tải ảnh album nghệ thuật bằng Glide
        Glide.with(this)
            .load(getAlbumArt(path))
            .placeholder(R.drawable.bg_avatar_circle)
            .error(R.drawable.bg_avatar_circle)
            .into(binding.imgSong)
    }

    private fun getAlbumArt(path: String): ByteArray? {
        return try {
            val retriever = android.media.MediaMetadataRetriever()
            retriever.setDataSource(path)
            val art = retriever.embeddedPicture
            retriever.release()
            art
        } catch (e: Exception) {
            null
        }
    }

    private fun nextSong() {
        exoPlayer?.seekToNextMediaItem()
    }

    private fun prevSong() {
        exoPlayer?.seekToPreviousMediaItem()
    }

    private fun startAnimation() {
        if (rotateSong?.isPaused == true) rotateSong?.resume() else rotateSong?.start()
    }

    private fun pauseAnimation() = rotateSong?.pause()

    private val updateProgress = object : Runnable {
        override fun run() {
            exoPlayer?.let {
                val current = it.currentPosition
                val duration = it.duration
                if (duration > 0) {
                    val progressFloat = current.toFloat() / duration.toFloat()
                    binding.waveformView.setProgress(progressFloat)
                }
                binding.txtCurrentTime.text = formatTime(current.toInt())
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun formatTime(ms: Int): String {
        if (ms < 0) return "00:00"
        val sec = ms / 1000
        val min = sec / 60
        return String.format("%02d:%02d", min, sec % 60)
    }

    private fun getFileName(path: String): String {
        return path.substringAfterLast('/')
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateProgress)
        exoPlayer?.release()
        exoPlayer = null
    }
}
