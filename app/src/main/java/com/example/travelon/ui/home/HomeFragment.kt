package com.example.travelon.ui.home

import TOPlace
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelon.R
import com.example.travelon.ui.adapters.PlacesAdapter
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.place_row.view.*


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var placesClient: PlacesClient

    private var placesAdapter: PlacesAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        setupSearchView(root)

        viewModel.getPlacesRepository().observe(this, Observer {
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

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        placesAdapter = PlacesAdapter(this.placesRecyclerView, onClickListener = this::getSelectedPlace, onFavouriteClickListener = this::favouriteSelectedPlace)
        this.placesRecyclerView.adapter = placesAdapter

        linearLayoutManager = LinearLayoutManager(this.placesRecyclerView.context, LinearLayoutManager.VERTICAL, false)
        this.placesRecyclerView.layoutManager = linearLayoutManager

        placesRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
    }

    private fun setupSearchView(view: View) {

        this.setHasOptionsMenu(true)
        //val toolbar = root.findViewById<Toolbar>(R.id.toolbar)
        //activity?.setActionBar(toolbar)
        this.toolbar = view.findViewById(R.id.toolbar) as Toolbar
        toolbar.inflateMenu(R.menu.menu_search)

        val searchView = toolbar.menu.findItem(R.id.action_search).actionView as SearchView
        searchView.queryHint = "Search places"

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.getPlacesRepository().observe(this@HomeFragment, Observer { response ->
                        Log.i(ContentValues.TAG, response.toString())

                        response?.let {
                            this@HomeFragment.placesAdapter?.run {
                                places.clear()
                                places.addAll(it)
                                //viewModel.isLoading.value = false
                                notifyDataSetChanged()
                            }
                        }
                    })
                }

                return true
            }
        })
    }


    fun getSelectedPlace(view: View, place: TOPlace) {
        Log.i(TAG, place.name)
    }

    fun favouriteSelectedPlace(view: View, place: TOPlace) {
        Log.i(TAG, view.isSelected.toString())
        place.favourite = view.button_favorite.isChecked
        viewModel.setFavourite(place, view.button_favorite.isChecked)
    }

    fun onPlaceSelected(place: TOPlace) {
        Log.i(ContentValues.TAG, "TOPlace: " + place)
    }
}