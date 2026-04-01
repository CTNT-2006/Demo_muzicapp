package com.example.demo_muzicapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.content.Intent

class MusicFragment : Fragment(R.layout.fragment_music) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var songList: ArrayList<Song>
    private lateinit var adapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        dbHelper = DatabaseHelper(requireContext())

        // Load toàn bộ bài hát
        songList = dbHelper.getAllSongs()
        adapter = SongAdapter(songList) { clickedSong ->

            val intent = Intent(requireContext(), PlayerActivity::class.java)

            intent.putExtra("title", clickedSong.title)
            intent.putExtra("file", clickedSong.file)

            val paths = ArrayList<String>()
            for (s in songList) {
                paths.add(s.file)
            }

            intent.putStringArrayListExtra("list", paths)


            intent.putExtra("index", songList.indexOf(clickedSong))

            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Search
        val searchView = activity?.findViewById<SearchView>(R.id.searchView)
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = songList.filter {
                    it.title.contains(newText ?: "", true)
                }
                adapter.updateList(filtered)
                return true
            }
        })
    }
}