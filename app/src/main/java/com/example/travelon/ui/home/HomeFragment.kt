package com.example.travelon.ui.home

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.travelon.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import android.content.ContentValues.TAG
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelon.data.model.TOPlace
import com.example.travelon.ui.adapters.PlacesAdapter
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), PlaceSelectionListener {

    private lateinit var viewModel: HomeViewModel
    private lateinit var placesClient: PlacesClient

    private var placesAdapter: PlacesAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel.placesList.observe(this, Observer {
            Log.i(TAG, it.toString())

            it?.let {
                placesAdapter!!.run {
                    places.clear()
                    places.addAll(it)
                    //viewModel.isLoading.value = false
                    notifyDataSetChanged()
                }
            }
        })

        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setOnPlaceSelectedListener(this)

        // Specify the types of place data to return.

        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))

        Places.initialize(activity!!.applicationContext, "AIzaSyDoKNSMuqSRV-2_ALnbSxquG2jE0Rdg6Kc")

        // Create a new Places client instance.
        placesClient = Places.createClient(this.context!!)

        fetchPlace("", "Reti")

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        placesAdapter = PlacesAdapter(this.placesRecyclerView, onClickListener = this::getSelectedPlace)
        this.placesRecyclerView.adapter = placesAdapter

        linearLayoutManager = LinearLayoutManager(this.placesRecyclerView.context, LinearLayoutManager.VERTICAL, false)
        this.placesRecyclerView.layoutManager = linearLayoutManager
    }

    fun getSelectedPlace(view: View, place: TOPlace) {
        Log.i(TAG, place.name)
    }

    private fun fetchPlace(placeId: String, placeName: String) {

        // Specify the fields to return.
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME)

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        var token = AutocompleteSessionToken.newInstance()

        // Create a RectangularBounds object.
        val bounds = RectangularBounds.newInstance(
            LatLng(-33.880490, 151.184363),
            LatLng(-33.858754, 151.229596))

        // Construct a request object, passing the place ID and fields array.
        val predictionRequest = FindAutocompletePredictionsRequest
            .builder()
            .setLocationBias(bounds)
            .setCountry("es")
            .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(token)
            .setQuery(placeName)
            .build()

        placesClient.findAutocompletePredictions(predictionRequest).addOnSuccessListener { response ->
            viewModel.postPlaces(response.autocompletePredictions)

            for (prediction in response.autocompletePredictions) {

                Log.i(TAG, prediction.placeId)
                Log.i(TAG, prediction.getPrimaryText(null).toString())
            }
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                Log.e(TAG, "TOPlace not found: " + exception.statusCode)
            }
        }

        val request = FetchPlaceRequest.builder(placeId, placeFields)
            .build()

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            Log.i(ContentValues.TAG, response.toString())
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                Log.e(ContentValues.TAG, "TOPlace not found: " + exception.statusCode)
            }
        }
    }

    override fun onPlaceSelected(p0: Place) {
        Log.i(ContentValues.TAG, "TOPlace: " + p0.getName() + ", " + p0.getId());
        print("Place2: " + p0.name)
    }

    override fun onError(p0: Status) {
        Log.i(ContentValues.TAG, "An error occurred: " + p0);
    }
}