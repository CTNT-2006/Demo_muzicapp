package com.example.demo_muzicapp.data.remote.model

import com.google.gson.annotations.SerializedName

data class SpotifyAuthResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int
)