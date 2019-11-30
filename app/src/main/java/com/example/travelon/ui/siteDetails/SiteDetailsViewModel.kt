package com.example.travelon.ui.siteDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelon.data.model.PlaceApiResponse
import com.example.travelon.data.model.Review
import com.example.travelon.data.model.TOPlace
import com.example.travelon.data.repository.PlacesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SiteDetailsViewModel : ViewModel() {
    private val repository: PlacesRepository = PlacesRepository.sharedInstance

    private var mutablePlace: MutableLiveData<TOPlace> = MutableLiveData<TOPlace>()
    val place: LiveData<TOPlace> = mutablePlace

    private val _isLoading = MutableLiveData<Boolean>().apply {
        value = false
    }
    val isLoading: LiveData<Boolean> = _isLoading

    val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    fun getPlace(placeId: String) {
        repository.getPlace(placeId) {
            mutablePlace.apply {
                value = it
            }
        }
    }

    fun getGooglePlace(placeId: String) {
        repository.getGooglePlace(placeId) {
            mutablePlace.apply {
                value = it?.results
            }
        }
    }

    fun createPlace(place: TOPlace, completionHandler: () -> Unit) {
        _isLoading.value = true
        repository.createPlace(place) {
            _isLoading.value = false
            completionHandler()
        }
    }

    fun createReview(place: TOPlace, review: Review, completionHandler: (TOPlace?) -> Unit) {
        _isLoading.value = true
        repository.createReview(review, place) {
            _isLoading.value = false
            completionHandler(it)
        }
    }
}
