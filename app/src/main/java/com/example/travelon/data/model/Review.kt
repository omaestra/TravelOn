package com.example.travelon.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Review(
    @SerializedName("author_name") val author_name: String = "",
    @SerializedName("author_url") val author_url: String? = "",
    @SerializedName("language") val language: String? = null,
    @SerializedName("profile_photo_url") val profile_photo_url: String? = null,
    @SerializedName("rating") val rating: Int? = null,
    @SerializedName("relative_time_description") val relative_time_description: String = "",
    @SerializedName("text") val text: String = "",
    @SerializedName("time") val time: Long = Date().time
)