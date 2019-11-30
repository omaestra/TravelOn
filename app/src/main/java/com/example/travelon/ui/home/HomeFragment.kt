package com.example.travelon.ui.home

import com.example.travelon.data.model.TOPlace
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelon.R
import com.example.travelon.data.model.PlaceActionType
import com.example.travelon.ui.adapters.PlacesAdapter
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.place_row.view.*


class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var placesClient: PlacesClient

    private lateinit var placesAdapter: PlacesAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        viewModel.isLoading.observe(this, Observer { isLoading ->
            loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.getPlacesRepository().observe(this, Observer {
            it?.let {
                placesAdapter.run {
                    places.clear()
                    places.addAll(it)
                    //viewModel.isLoading.value = false
                    notifyDataSetChanged()
                }
            }
        })

        viewModel.filteredPlaces.observe(this, Observer {
            it?.let {
                placesAdapter.run {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search Places"

        searchView.setOnCloseListener(object: SearchView.OnCloseListener {
            override fun onClose(): Boolean {

                return true
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    return false
                }
                viewModel.filterPlaces(newText)

                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupViews() {
        placesAdapter = PlacesAdapter(this.placesRecyclerView, onClickListener = this::getSelectedPlace, onFavouriteClickListener = this::favouriteSelectedPlace)
        this.placesRecyclerView.adapter = placesAdapter

        linearLayoutManager = LinearLayoutManager(this.placesRecyclerView.context, LinearLayoutManager.VERTICAL, false)
        this.placesRecyclerView.layoutManager = linearLayoutManager

        placesRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        placesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                checkEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkEmpty()
            }

            fun checkEmpty() {
                empty_view.visibility = (if (placesAdapter.itemCount == 0) View.VISIBLE else View.GONE)
            }
        })
    }

    private fun setupSearchView(view: View) {

        //this.setHasOptionsMenu(true)
        //val toolbar = root.findViewById<Toolbar>(R.com.example.travelon.data.model.getId.toolbar)
        //activity?.setActionBar(toolbar)
        //this.toolbar = view.findViewById(R.com.example.travelon.data.model.getId.toolbar) as Toolbar
        //toolbar.inflateMenu(R.menu.menu_search)

//        val searchView = toolbar.menu.findItem(R.com.example.travelon.data.model.getId.action_search).actionView as SearchView
//        searchView.queryHint = "Search places"
//
//        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let {
//                    viewModel.getPlacesRepository().observe(this@HomeFragment, Observer { response ->
//                        Log.i(ContentValues.TAG, response.toString())
//
//                        response?.let {
//                            this@HomeFragment.placesAdapter?.run {
//                                places.clear()
//                                places.addAll(it)
//                                //viewModel.isLoading.value = false
//                                notifyDataSetChanged()
//                            }
//                        }
//                    })
//                }
//
//                return true
//            }
//        })
    }


    fun getSelectedPlace(view: View, place: TOPlace) {
        Log.i(TAG, place.name)

        val action = HomeFragmentDirections.actionNavigationHomeToSiteDetailsFragment()

        val bundle = bundleOf("place" to place, "placeActionType" to PlaceActionType.VIEW)

        findNavController().navigate(action.actionId, bundle)

    }

    fun favouriteSelectedPlace(view: View, place: TOPlace) {
        Log.i(TAG, view.isSelected.toString())
        place.favourite = view.button_favorite.isChecked
        viewModel.setFavourite(place, view.button_favorite.isChecked)
    }

    fun onPlaceSelected(place: TOPlace) {
        Log.i(ContentValues.TAG, "com.example.travelon.data.model.TOPlace: " + place)
    }
}