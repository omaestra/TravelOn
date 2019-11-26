package com.example.travelon.ui.createSite

import TOPlace
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelon.R
import com.example.travelon.ui.adapters.GooglePlacesAdapter
import kotlinx.android.synthetic.main.fragment_favourites.*


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
                    .observe(this@CreateSiteFragment, Observer { response ->
                        Log.i(ContentValues.TAG, response.results.toString())

                        response?.let {
                            this@CreateSiteFragment.placesAdapter.run {
                                places.clear()
                                places.addAll(it.results)
                                //viewModel.isLoading.value = false
                                notifyDataSetChanged()
                            }
                        }
                    })
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
    }

    private fun setupSearchView(view: View) {
        //this.setHasOptionsMenu(true)
        //val toolbar = root.findViewById<Toolbar>(R.id.toolbar)
        //activity?.setActionBar(toolbar)

//        toolbar = view.findViewById(R.id.toolbar) as Toolbar
//        toolbar.inflateMenu(R.menu.menu_search)
//
//        val searchView = toolbar.menu.findItem(R.id.action_search).actionView as SearchView
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
        print("CREATE PLACE")
    }

}