package com.example.travelon.ui.home

import TOPlace
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelon.data.model.PlaceApiResponse
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

    init {
        placesRepository = PlacesRepository.sharedInstance
        //mutablePlacesList = placesRepository?.getPlaces("mad")!!

        mutablePlacesList = placesRepository?.getPlaces()!!
    }

    fun getPlacesRepository(): MutableLiveData<List<TOPlace>> {
        return mutablePlacesList
    }

    fun postPlaces(placesList: List<AutocompletePrediction>) {
        //mutablePlacesList.apply {
        //    value = placesList.map { TOPlace(it.getPrimaryText(null).toString(),
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