package com.example.demo_muzicapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class SpotifySearchResponse(
    @SerializedName("tracks") val tracks: SpotifyTracks
)

data class SpotifyTracks(
    @SerializedName("items") val items: List<SpotifyTrack>
)

data class SpotifyTrack(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("artists") val artists: List<SpotifyArtist>,
    @SerializedName("album") val album: SpotifyAlbum,
    @SerializedName("preview_url") val previewUrl: String?
)

data class SpotifyArtist(
    @SerializedName("name") val name: String
)

data class SpotifyAlbum(
    @SerializedName("name") val name: String,
    @SerializedName("images") val images: List<SpotifyImage>
)

data class SpotifyImage(
    @SerializedName("url") val url: String,
    @SerializedName("height") val height: Int,
    @SerializedName("width") val width: Int
)