package com.example.demo_muzicapp.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.demo_muzicapp.data.local.DatabaseHelper
import com.example.demo_muzicapp.data.model.Song

class MusicRepository(private val dbHelper: DatabaseHelper) {
    fun getAllSongs(): List<Song> = dbHelper.getAllSongs()
    fun getHistorySongs(): List<Song> = dbHelper.getHistorySongs()
    fun insertSong(song: Song) = dbHelper.insertSong(song)
    fun insertHistory(songId: Int) = dbHelper.insertHistory(songId)
    fun register(username: String, password: String) = dbHelper.register(username, password)
    fun login(username: String, password: String) = dbHelper.login(username, password)
    fun updateFavorite(songId: Int, isFavorite: Boolean) = dbHelper.updateFavorite(songId, isFavorite)
    fun getFavoriteSongs(): List<Song> = dbHelper.getFavoriteSongs()

    fun getLocalSongs(context: Context): List<Song> {
        val songList = mutableListOf<Song>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )

        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getInt(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val path = cursor.getString(dataColumn)
                val albumId = cursor.getLong(albumIdColumn)
                
                val artUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                ).toString()

                songList.add(Song(id, title, artist, artUri, "", path))
            }
        }
        return songList
    }
}