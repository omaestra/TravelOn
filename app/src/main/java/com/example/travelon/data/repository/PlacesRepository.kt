package com.example.travelon.data.repository

import TOPlace
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.travelon.data.model.PlaceApiResponse
import com.example.travelon.services.GooglePlacesAPI
import com.example.travelon.services.RetrofitService
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class PlacesRepository {
    private var placesRepository: PlacesRepository? = null

    private object Holder { val INSTANCE = PlacesRepository() }

    companion object {
        val sharedInstance: PlacesRepository by lazy { Holder.INSTANCE }
    }

    private var googlePlacesApi: GooglePlacesAPI? = null
    private lateinit var database: DatabaseReference

    private val ApiKey = "AIzaSyBla0a-Mh6b3Vr2h8z9vl3__Cpq6rzGCsY"

    init {
        googlePlacesApi = RetrofitService.createService(GooglePlacesAPI::class.java)
        database = FirebaseDatabase.getInstance().reference
    }

    fun getGooglePlaces(query: String): MutableLiveData<PlaceApiResponse> {
        val placesData = MutableLiveData<PlaceApiResponse>()

        googlePlacesApi?.getPlacesList(query, region = "es", language = "es", apiKey = ApiKey)?.enqueue(object : Callback<PlaceApiResponse> {
            override fun onResponse(call: Call<PlaceApiResponse>, response: Response<PlaceApiResponse>) {
                print("RESPONSE" + response.toString())

                if (response.isSuccessful) {
                    placesData.value = response.body()
                    placesData.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<PlaceApiResponse>, t: Throwable) {
                placesData.value = null
            }
        })

        return placesData
    }

    fun createPlace(place: TOPlace) {
        val key = database.child("places").push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }

        val placeValues = place.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/favouritePlaces/$key"] = placeValues

        database.updateChildren(childUpdates)
    }

    fun getPlaces(): MutableLiveData<List<TOPlace>> {
        val placesData = MutableLiveData<List<TOPlace>>()

        val placesReference = database.child("places")
        placesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list : MutableList<TOPlace> = mutableListOf()

                dataSnapshot.children.forEach {
                    list.add(it.getValue(TOPlace::class.java)!!)
                }

                placesData.value = list
                placesData.postValue(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        return placesData
    }

    fun getFavouritePlaces(): MutableLiveData<List<TOPlace>> {
        val placesData = MutableLiveData<List<TOPlace>>()

        val favouritePlacesRef = database.child("places").orderByChild("favourite").equalTo(true)
        favouritePlacesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list : MutableList<TOPlace> = mutableListOf()

                dataSnapshot.children.forEach {
                    list.add(it.getValue(TOPlace::class.java)!!)
                }
//                val place = dataSnapshot.getValue(TOPlace::class.java)
//                if (place != null) {
//                    list.add(place)
//                }

                placesData.value = list
                placesData.postValue(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        return placesData
    }

    fun setFavourite(place: TOPlace) {
        database.child("places").child(place.id).setValue(place)
            .addOnSuccessListener {
                Log.i(TAG, "Favourite place set.")
            }
            .addOnFailureListener {
                Log.e(TAG, "Error setting place as favourite")
            }
    }

    private fun onFavouriteClicked(place: TOPlace, postRef: DatabaseReference) {
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(TOPlace::class.java)

                if (p == null) {
                    mutableData.value = place
                } else {
                    mutableData.value = null
                }

                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                b: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError.toString())
            }
        })
    }

}