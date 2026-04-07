package com.example.demo_muzicapp.ui.activities

import android.animation.ObjectAnimator
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.demo_muzicapp.R
import com.example.demo_muzicapp.data.model.Song
import com.example.demo_muzicapp.databinding.ActivityMainBinding
import com.example.demo_muzicapp.ui.fragments.DiscoveryFragment
import com.example.demo_muzicapp.ui.fragments.MusicFragment
import com.example.demo_muzicapp.ui.fragments.ProfileFragment
import com.example.demo_muzicapp.viewmodel.PlayerViewModel
import com.example.demo_muzicapp.ui.views.WaveformView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val playerViewModel: PlayerViewModel by viewModels()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var rotateSong: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPlayer()
        setupBottomNav(savedInstanceState)
    }

    private fun setupPlayer() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playerContainer.playerRoot)
        
        // Peek height = mini player height (70dp) + bottom nav height (80dp)
        val density = resources.displayMetrics.density
        bottomSheetBehavior.peekHeight = ((70 + 80) * density).toInt()
        
        // Hide player initially
        binding.playerContainer.playerRoot.visibility = View.GONE

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.playerContainer.miniPlayer.alpha = 0f
                    binding.playerContainer.expandedPlayer.alpha = 1f
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.playerContainer.miniPlayer.alpha = 1f
                    binding.playerContainer.expandedPlayer.alpha = 0f
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.playerContainer.miniPlayer.alpha = 1 - slideOffset
                binding.playerContainer.expandedPlayer.alpha = slideOffset
            }
        })

        // Animation for disc
        rotateSong = ObjectAnimator.ofFloat(binding.playerContainer.cardDisc, "rotation", 0f, 360f).apply {
            duration = 15000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }

        // Observers
        playerViewModel.currentSong.observe(this) { song ->
            if (song != null) {
                binding.playerContainer.playerRoot.visibility = View.VISIBLE
                updatePlayerUI(song)
                binding.playerContainer.waveformView.setSeed(song.id.toLong())
            }
        }

        playerViewModel.isPlaying.observe(this) { isPlaying ->
            val icon = if (isPlaying) R.drawable.ic_pause_vector else R.drawable.ic_play_vector
            binding.playerContainer.btnPlay.setImageResource(icon)
            binding.playerContainer.btnMiniPlay.setImageResource(icon)
            if (isPlaying) {
                if (rotateSong?.isStarted == true) rotateSong?.resume() else rotateSong?.start()
            } else {
                rotateSong?.pause()
            }
        }

        playerViewModel.progress.observe(this) { progress ->
            val duration = playerViewModel.duration.value ?: 1L
            val progressFloat = if (duration > 0) progress.toFloat() / duration.toFloat() else 0f
            
            binding.playerContainer.waveformView.setProgress(progressFloat)
            binding.playerContainer.miniProgress.progress = progress.toInt()
            binding.playerContainer.txtCurrentTime.text = formatTime(progress.toInt())
        }

        playerViewModel.duration.observe(this) { duration ->
            binding.playerContainer.miniProgress.max = duration.toInt()
            binding.playerContainer.txtTotalTime.text = formatTime(duration.toInt())
        }

        playerViewModel.isRepeatOne.observe(this) { isRepeat ->
            val tint = if (isRepeat) 0xFF42A5F5.toInt() else 0xB3FFFFFF.toInt()
            binding.playerContainer.btnRepeat.setColorFilter(tint)
        }

        playerViewModel.isShuffle.observe(this) { isShuffle ->
            val tint = if (isShuffle) 0xFF42A5F5.toInt() else 0xB3FFFFFF.toInt()
            binding.playerContainer.btnShuffle.setColorFilter(tint)
        }

        playerViewModel.isFavorite.observe(this) { isFavorite ->
            val icon = if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_border
            binding.playerContainer.btnFavorite.setImageResource(icon)
            binding.playerContainer.btnFavorite.setColorFilter(if (isFavorite) 0xFFFF4081.toInt() else 0xFFFFFFFF.toInt())
        }

        // Waveform Progress
        binding.playerContainer.waveformView.setOnProgressChangedListener(object : WaveformView.OnProgressChangedListener {
            override fun onProgressChanged(progress: Float, fromUser: Boolean) {
                if (fromUser) {
                    val duration = playerViewModel.duration.value ?: 0L
                    playerViewModel.seekTo((progress * duration).toLong())
                }
            }
        })

        // Controls
        binding.playerContainer.btnPlay.setOnClickListener { playerViewModel.togglePlayPause() }
        binding.playerContainer.btnMiniPlay.setOnClickListener { playerViewModel.togglePlayPause() }
        binding.playerContainer.btnNext.setOnClickListener { playerViewModel.next() }
        binding.playerContainer.btnRepeat.setOnClickListener { playerViewModel.toggleRepeat() }
        binding.playerContainer.btnShuffle.setOnClickListener { playerViewModel.toggleShuffle() }
        binding.playerContainer.btnFavorite.setOnClickListener { playerViewModel.toggleFavorite() }
        binding.playerContainer.btnExpand.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.playerContainer.btnPrevious.setOnClickListener { playerViewModel.previous() }
        binding.playerContainer.btnCollapse.setOnClickListener { 
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED 
        }

        // Update progress regularly
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                playerViewModel.updateProgress()
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun updatePlayerUI(song: Song) {
        binding.playerContainer.songTitle.text = song.title
        binding.playerContainer.artist.text = song.artist
        binding.playerContainer.txtMiniTitle.text = song.title
        binding.playerContainer.txtMiniArtist.text = song.artist

        val albumArt = getAlbumArt(song.file)

        Glide.with(this)
            .load(albumArt)
            .placeholder(R.drawable.bg_avatar_circle)
            .error(R.drawable.bg_avatar_circle)
            .into(binding.playerContainer.imgSong)

        Glide.with(this)
            .load(albumArt)
            .placeholder(R.drawable.bg_avatar_circle)
            .error(R.drawable.bg_avatar_circle)
            .into(binding.playerContainer.imgMiniSong)
    }

    private fun getAlbumArt(path: String): ByteArray? {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(path)
            val art = retriever.embeddedPicture
            retriever.release()
            art
        } catch (e: Exception) {
            null
        }
    }

    private fun formatTime(ms: Int): String {
        val sec = ms / 1000
        val min = sec / 60
        return String.format(Locale.getDefault(), "%02d:%02d", min, sec % 60)
    }

    private fun setupBottomNav(savedInstanceState: Bundle?) {
        binding.bottomNav.setOnItemSelectedListener { item ->
            // Thu nhỏ player nếu đang mở rộng khi chuyển trang
            if (::bottomSheetBehavior.isInitialized && 
                bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            showLoading()
            val fragment: Fragment = when(item.itemId) {
                R.id.nav_music -> MusicFragment()
                R.id.nav_discovery -> DiscoveryFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> MusicFragment()
            }
            loadFragment(fragment)
            true
        }

        // Load mặc định
        if (savedInstanceState == null) {
            binding.bottomNav.selectedItemId = R.id.nav_music
        }

        // Nếu mở trực tiếp profile từ notification / intent
        val openProfile = intent.getBooleanExtra("openProfile", false)
        if (openProfile) {
            binding.bottomNav.selectedItemId = R.id.nav_profile
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            .replace(R.id.frameContainer, fragment)
            .commit()
        hideLoading()
    }

    private fun showLoading() {
        binding.loading.visibility = View.VISIBLE
        val anim = AlphaAnimation(0f, 1f)
        anim.duration = 300
        binding.loading.startAnimation(anim)
    }

    private fun hideLoading() {
        binding.loading.postDelayed({
            val anim = AlphaAnimation(1f, 0f)
            anim.duration = 300
            binding.loading.startAnimation(anim)
            binding.loading.visibility = View.GONE
        }, 500)
    }
}
