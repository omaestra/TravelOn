package com.example.travelon.ui.createSite

import com.example.travelon.data.model.TOPlace
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
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
import com.example.travelon.ui.adapters.GooglePlacesAdapter
import com.example.travelon.ui.home.HomeFragmentDirections
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.fragment_home.*


class CreateSiteFragment : Fragment() {

    private lateinit var viewModel: CreateSiteViewModel
    private lateinit var placesAdapter: GooglePlacesAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var searchView: SearchView

    lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProviders.of(this).get(CreateSiteViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_create_site, container, false)

        val loading = root.findViewById<ProgressBar>(R.id.loading)

        viewModel.isLoading.observe(this, Observer { isLoading ->
            loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        viewModel.placesList.observe(this@CreateSiteFragment, Observer { response ->
            Log.i(ContentValues.TAG, response.results.toString())

            response?.let {
                this@CreateSiteFragment.placesAdapter.run {
                    places.clear()
                    it.results?.let { it1 -> places.addAll(it1) }
                    //viewModel.isLoading.value = false
                    notifyDataSetChanged()
                }
            }
        })

        setHasOptionsMenu(true)

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
        searchView.queryHint = "Search Google Places"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.fetchPlaces(query)

                searchView.clearFocus()

                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupViews() {
        placesAdapter = GooglePlacesAdapter(
            this.googlePlacesRecyclerView,
            onClickListener = this::getSelectedPlace,
            onCreatePlaceClickListener = this::createPlace
        )
        this.googlePlacesRecyclerView.adapter = placesAdapter

        linearLayoutManager = LinearLayoutManager(this.googlePlacesRecyclerView.context, LinearLayoutManager.VERTICAL, false)
        this.googlePlacesRecyclerView.layoutManager = linearLayoutManager

        googlePlacesRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

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

//        toolbar = view.findViewById(R.com.example.travelon.data.model.getId.toolbar) as Toolbar
//        toolbar.inflateMenu(R.menu.menu_search)
//
//        val searchView = toolbar.menu.findItem(R.com.example.travelon.data.model.getId.action_search).actionView as SearchView
//        searchView.queryHint = "Search places from Google"
//
//        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return true
//            }
//


//            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let {
//                    viewModel.fetchPlaces(it).observe(this@CreateSiteFragment, Observer { response ->
//                        Log.i(ContentValues.TAG, response.results.toString())
//
//                        response?.let {
//                            this@CreateSiteFragment.placesAdapter.run {
//                                places.clear()
//                                places.addAll(it.results)
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
        print("SELECTED PLACE")
    }

    fun createPlace(view: View, place: TOPlace) {
        val action = CreateSiteFragmentDirections.actionNavigationNotificationsToSiteDetailsFragment()

        val bundle = bundleOf("place" to place, "placeActionType" to PlaceActionType.CREATE)

        findNavController().navigate(action.actionId, bundle)
    }

}