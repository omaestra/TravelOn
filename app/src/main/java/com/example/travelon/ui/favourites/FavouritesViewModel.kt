package com.example.travelon.ui.favourites

import com.example.travelon.data.model.TOPlace
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelon.data.repository.PlacesRepository

class FavouritesViewModel : ViewModel() {

    private var placesRepository: PlacesRepository? = null

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    private var mutablePlacesList: MutableLiveData<List<TOPlace>> = MutableLiveData()
    var placesList: LiveData<List<TOPlace>> = mutablePlacesList

    private val _isLoading = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        placesRepository = PlacesRepository.sharedInstance
        //mutablePlacesList = placesRepository?.getPlaces("mad")!!

        _isLoading.value = true
        placesRepository?.getFavouritePlaces {
            _isLoading.value = false

            mutablePlacesList.value = it
            mutablePlacesList.postValue(it)
        }
    }

    fun getFavouritePlacesRepository(): MutableLiveData<List<TOPlace>> {
        return mutablePlacesList
    }
}