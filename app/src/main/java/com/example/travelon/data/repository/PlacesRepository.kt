package com.example.travelon.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.example.travelon.data.model.PlaceApiResponse
import com.example.travelon.data.model.Review
import com.example.travelon.data.model.TOPlace
import com.example.travelon.services.GooglePlacesAPI
import com.example.travelon.services.RetrofitService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PlacesRepository {
    private var placesRepository: PlacesRepository? = null

    private object Holder { val INSTANCE = PlacesRepository() }

    companion object {
        val sharedInstance: PlacesRepository by lazy { Holder.INSTANCE }
    }

    private var googlePlacesApi: GooglePlacesAPI? = null
    private var database: DatabaseReference

    private val ApiKey = "AIzaSyBla0a-Mh6b3Vr2h8z9vl3__Cpq6rzGCsY"

    init {
        googlePlacesApi = RetrofitService.createService(GooglePlacesAPI::class.java)
        database = FirebaseDatabase.getInstance().reference
    }

    fun getGooglePlaces(query: String, completionHandler: (PlaceApiResponse<List<TOPlace>>?) -> Unit) {
        googlePlacesApi?.getPlacesList(query, region = "es", language = "es", apiKey = ApiKey)?.enqueue(object : Callback<PlaceApiResponse<List<TOPlace>>> {
            override fun onResponse(call: Call<PlaceApiResponse<List<TOPlace>>>, response: Response<PlaceApiResponse<List<TOPlace>>>) {
                if (response.isSuccessful) {
                    completionHandler(response.body())
                }
            }

            override fun onFailure(call: Call<PlaceApiResponse<List<TOPlace>>>, t: Throwable) {
                completionHandler(null)
            }
        })
    }

    fun getGooglePlace(placeId: String, completionHandler: (PlaceApiResponse<TOPlace>?) -> Unit) {
        googlePlacesApi?.getPlace(placeId, apiKey = ApiKey)?.enqueue(object : Callback<PlaceApiResponse<TOPlace>> {
            override fun onResponse(
                call: Call<PlaceApiResponse<TOPlace>>,
                response: Response<PlaceApiResponse<TOPlace>>
            ) {
                completionHandler(response.body())
            }

            override fun onFailure(call: Call<PlaceApiResponse<TOPlace>>, t: Throwable) {
                completionHandler(null)
            }
        })
    }

    fun getPlace(placeId: String, completionHandler: (TOPlace?) -> Unit) {
        val placeRef = database.child("places").orderByChild("place_id").equalTo(placeId).limitToFirst(1)
        placeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val place = dataSnapshot.children.first().getValue(TOPlace::class.java)

                completionHandler(place)
            }

            override fun onCancelled(p0: DatabaseError) {
                completionHandler(null)
            }
        })
    }

    fun createPlace(place: TOPlace, completionHandler: (Boolean) -> Unit) {
        val key = database.child("places").push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        place.user = user?.email

        place.id = key
        val placeValues = place.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/places/$key"] = placeValues

        database.updateChildren(childUpdates) { _, _ ->
            completionHandler(true)
        }
    }

    fun getPlaces(completionHandler: (MutableList<TOPlace>?) -> Unit) {
        val placesReference = database.child("places")
        placesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list : MutableList<TOPlace> = mutableListOf()

                dataSnapshot.children.forEach {
                    it.getValue(TOPlace::class.java)?.let { newPlace ->
                        list.add(newPlace)
                    }
                }

                completionHandler(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                completionHandler(null)
            }
        })
    }

    fun getFavouritePlaces(completionHandler: (List<TOPlace>?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser?.email ?: ""

        val favouritePlacesRef = database.child("places")
            .orderByChild("user")
            .equalTo(user)
        favouritePlacesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list : MutableList<TOPlace> = mutableListOf()

                dataSnapshot.children.forEach {
                    it.getValue(TOPlace::class.java)?.let { newPlace ->
                        if (newPlace.favourite) list.add(newPlace)
                    }
                }

                completionHandler(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                completionHandler(null)
            }
        })
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

    fun createReview(review: Review, place: TOPlace, completionHandler: (TOPlace?) -> Unit) {
        place.reviews?.add(review)

        database.child("places").child(place.id).child("reviews").setValue(place.reviews)
            .addOnSuccessListener {
                Log.i(TAG, "Review added and Place updated.")
                completionHandler(place)
            }
            .addOnFailureListener {
                Log.e(TAG, "Error updating Place reviews.")
                completionHandler(null)
            }
    }
}