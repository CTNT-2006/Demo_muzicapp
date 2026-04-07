package com.example.demo_muzicapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MusicFragment : Fragment(R.layout.fragment_music) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var songList: ArrayList<Song>
    private lateinit var adapter: SongAdapter
    private var currentList: ArrayList<Song> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Database
        dbHelper = DatabaseHelper(requireContext())
        songList = dbHelper.getAllSongs()
        currentList = ArrayList(songList)

        // Adapter
        adapter = SongAdapter(currentList) { clickedSong ->
            val index = currentList.indexOf(clickedSong)
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("title", clickedSong.title)
            intent.putExtra("file", clickedSong.file)
            intent.putStringArrayListExtra("list", ArrayList(currentList.map { it.file }))
            intent.putExtra("index", index)
            startActivity(intent)
        }

        recyclerView.adapter = adapter


        val searchView = view.findViewById<SearchView>(R.id.searchViewMusic)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentList = if (!newText.isNullOrEmpty()) {
                    ArrayList(songList.filter { it.title.contains(newText, ignoreCase = true) })
                } else {
                    ArrayList(songList)
                }
                adapter.updateList(currentList)
                return true
            }
        })
    }
}