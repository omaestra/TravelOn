package com.example.travelon.data.model

import com.google.gson.annotations.SerializedName

data class PlaceApiResponse<T> (
    @SerializedName("html_attributions") val html_attributions : List<String>,
    @SerializedName("results", alternate = ["result"])  val results : T,
    @SerializedName("status")  val status : String
)