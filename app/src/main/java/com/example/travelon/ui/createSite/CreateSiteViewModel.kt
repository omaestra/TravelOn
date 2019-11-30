package com.example.travelon.ui.createSite

import com.example.travelon.data.model.TOPlace
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

    private val _isLoading = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isLoading: LiveData<Boolean> = _isLoading

    private var mutablePlacesList: MutableLiveData<PlaceApiResponse<List<TOPlace>>> = MutableLiveData()
    var placesList: LiveData<PlaceApiResponse<List<TOPlace>>> = mutablePlacesList

    init {
        placesRepository = PlacesRepository.sharedInstance
    }

    fun fetchPlaces(query: String) {
        _isLoading.value = true

        placesRepository?.getGooglePlaces(query) {
            _isLoading.value = false

            mutablePlacesList.value = it
            mutablePlacesList.postValue(it)
        }
        //mutablePlacesList = placesRepository?.getGooglePlaces(query)!!
        //return mutablePlacesList
    }

    fun createPlace(place: TOPlace) {
        placesRepository?.createPlace(place) {

        }
    }
}