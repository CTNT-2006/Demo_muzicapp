package com.example.demo_muzicapp.data.remote.api

import com.example.demo_muzicapp.data.remote.model.SpotifySearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SpotifyApi {
    @GET("v1/search")
    fun searchTracks(
        @Header("Authorization") authHeader: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 20
    ): Call<SpotifySearchResponse>
}