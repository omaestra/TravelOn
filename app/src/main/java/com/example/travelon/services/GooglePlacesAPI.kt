package com.example.travelon.services

import com.example.travelon.data.model.PlaceApiResponse
import com.example.travelon.data.model.TOPlace
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

public interface GooglePlacesAPI {
    @GET("place/textsearch/json")
    fun getPlacesList(
        @Query("query") query: String,
        @Query("region") region: String,
        @Query("language") language: String,
        @Query("key") apiKey: String
    ): Call<PlaceApiResponse<List<TOPlace>>>

    @GET("place/details/json")
    fun getPlace(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String
    ): Call<PlaceApiResponse<TOPlace>>

    @GET("place/photo")
    fun getPhoto(
        @Query("maxwidth") maxwidth: String,
        @Query("photoreference") photoreference: String
    ): Call<Base64>
}