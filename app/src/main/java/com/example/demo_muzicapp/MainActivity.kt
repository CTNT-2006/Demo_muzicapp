package com.example.demo_muzicapp
import com.example.demo_muzicapp.R
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        addSampleSong()


        val toolbar = findViewById<Toolbar?>(R.id.toolbar)
        toolbar?.let {
            setSupportActionBar(it)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        // ✅ SearchView an toàn
        val searchView = findViewById<SearchView?>(R.id.searchView)
        searchView?.let {
            val searchText = it.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            searchText?.setTextColor(Color.WHITE)
            searchText?.setHintTextColor(Color.parseColor("#AAFFFFFF"))
            it.queryHint = "Tìm bài hát..."
        }

        // ✅ BottomNav an toàn
        val bottomNav = findViewById<BottomNavigationView?>(R.id.bottomNav)
        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_music -> {
                    loadFragment(MusicFragment())
                    true
                }
                R.id.nav_playlist -> {
                    loadFragment(PlaylistFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            loadFragment(MusicFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit()
    }

    private fun addSampleSong() {
        val list = dbHelper.getAllSongs()

        if (list.isEmpty()) {
            dbHelper.insertSong(
                Song(0, "See You Again", "Wiz Khalifa ft. Charlie Puth", "", "",
                    "android.resource://" + packageName + "/" + R.raw.song1)
            )

            dbHelper.insertSong(
                Song(0, "Faded", "Alan Walker", "", "",
                    "android.resource://" + packageName + "/" + R.raw.song2)
            )

            dbHelper.insertSong(
                Song(0, "Believer", "Imagine Dragons", "", "",
                    "android.resource://" + packageName + "/" + R.raw.song3)
            )
        }
    }

}