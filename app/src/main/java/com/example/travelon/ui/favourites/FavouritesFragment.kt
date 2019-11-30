package com.example.travelon.ui.favourites

import com.example.travelon.data.model.TOPlace
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelon.R
import com.example.travelon.ui.adapters.FavouritePlaceAdapter
import kotlinx.android.synthetic.main.fragment_favourites.*

class FavouritesFragment : Fragment() {

    private lateinit var viewModel: FavouritesViewModel

    private var placesAdapter: FavouritePlaceAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(FavouritesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_favourites, container, false)

        viewModel.getFavouritePlacesRepository().observe(this, Observer {
            Log.i(ContentValues.TAG, it.toString())

            it?.let {
                placesAdapter!!.run {
                    places.clear()
                    places.addAll(it)
                    //viewModel.isLoading.value = false
                    notifyDataSetChanged()
                }
            }
        })

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        placesAdapter = FavouritePlaceAdapter(
            this.googlePlacesRecyclerView,
            onClickListener = this::getSelectedPlace,
            onCreatePlaceClickListener = this::createPlace
        )
        this.googlePlacesRecyclerView.adapter = placesAdapter

        linearLayoutManager = LinearLayoutManager(this.googlePlacesRecyclerView.context, LinearLayoutManager.VERTICAL, false)
        this.googlePlacesRecyclerView.layoutManager = linearLayoutManager

        googlePlacesRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    }

    fun getSelectedPlace(view: View, place: TOPlace) {
        Log.i(TAG, place.name)
    }

    fun createPlace(view: View, place: TOPlace) {
        Log.i(TAG, place.name)
    }
}