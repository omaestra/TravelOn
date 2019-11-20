package com.example.travelon.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelon.data.model.TOPlace
import com.google.android.libraries.places.api.model.AutocompletePrediction

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    private val mutablePlacesList: MutableLiveData<List<TOPlace>> = MutableLiveData()
    var placesList: LiveData<List<TOPlace>> = mutablePlacesList

    init {
        println("First initializer block that prints ${text}")
    }

    fun postPlaces(placesList: List<AutocompletePrediction>) {
        mutablePlacesList.apply {
            value = placesList.map { TOPlace(it.getPrimaryText(null).toString(),
                it.getSecondaryText(null).toString(), false)
            }
        }
    }
}