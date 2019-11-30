package com.example.travelon.ui.home

import com.example.travelon.data.model.TOPlace
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelon.data.repository.PlacesRepository
import com.google.android.libraries.places.api.model.AutocompletePrediction

class HomeViewModel : ViewModel() {

    private var placesRepository: PlacesRepository? = null

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private var mutablePlacesList: MutableLiveData<List<TOPlace>> = MutableLiveData()
    var placesList: LiveData<List<TOPlace>> = mutablePlacesList

    private val _filteredPlaces = MutableLiveData<List<TOPlace>>()
    var filteredPlaces: LiveData<List<TOPlace>> = _filteredPlaces

    private val _isLoading = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        placesRepository = PlacesRepository.sharedInstance
        //mutablePlacesList = placesRepository?.getPlaces("mad")!!

        _isLoading.value = true
        placesRepository?.getPlaces {
            _isLoading.value = false

            mutablePlacesList.value = it
            mutablePlacesList.postValue(it)
        }
    }

    fun getPlacesRepository(): MutableLiveData<List<TOPlace>> {
        return mutablePlacesList
    }

    fun filterPlaces(byName: String) {
        _filteredPlaces.apply {
            value = mutablePlacesList.value?.filter {
                it.name.toLowerCase().contains(byName.toLowerCase())
            }
        }
    }

    fun postPlaces(placesList: List<AutocompletePrediction>) {
        //mutablePlacesList.apply {
        //    value = placesList.map { com.example.travelon.data.model.TOPlace(it.getPrimaryText(null).toString(),
        //        it.getSecondaryText(null).toString(), false)
        //    }
        //}
    }

    fun getPlaces(byText: String) {

    }

    fun setFavourite(place: TOPlace, favourite: Boolean) {
        placesRepository?.setFavourite(place)
    }
}