package com.example.travelon.ui.createSite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelon.data.model.PlaceApiResponse
import com.example.travelon.data.repository.PlacesRepository

class CreateSiteViewModel : ViewModel() {

    private var placesRepository: PlacesRepository? = null

    private val _text = MutableLiveData<String>().apply {
        value = "This is Create Site Fragment"
    }
    val text: LiveData<String> = _text

    private var mutablePlacesList: MutableLiveData<PlaceApiResponse> = MutableLiveData()
    var placesList: LiveData<PlaceApiResponse> = mutablePlacesList

    init {
        placesRepository = PlacesRepository.sharedInstance
    }

    fun fetchPlaces(query: String): MutableLiveData<PlaceApiResponse> {
        mutablePlacesList = placesRepository?.getGooglePlaces(query)!!
        return mutablePlacesList
    }
}