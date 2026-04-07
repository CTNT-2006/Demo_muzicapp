package com.example.demo_muzicapp.data.model

data class Song(
    val id: Int,
    val title: String,
    val artist: String,
    val image: String,
    val lyric: String,
    val file: String,
    var isFavorite: Boolean = false
)