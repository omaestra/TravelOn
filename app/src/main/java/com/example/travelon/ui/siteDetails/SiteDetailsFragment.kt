package com.example.travelon.ui.siteDetails

import android.os.Bundle
import android.view.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelon.R
import com.example.travelon.data.model.PlaceActionType
import com.example.travelon.data.model.Review
import com.example.travelon.data.model.TOPlace
import com.example.travelon.ui.adapters.PlaceCommentsAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.site_details_fragment.*
import java.util.*


class SiteDetailsFragment : Fragment(), OnMapReadyCallback {
    companion object {
        fun newInstance() = SiteDetailsFragment()
    }

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private var menu: Menu? = null

    private lateinit var viewModel: SiteDetailsViewModel
    private lateinit var adapter: PlaceCommentsAdapter

    var place: TOPlace? = null
    var placeActionType: PlaceActionType = PlaceActionType.VIEW

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val root = inflater.inflate(R.layout.site_details_fragment, container, false)

        place = arguments?.getSerializable("place") as TOPlace?
        placeActionType = arguments?.get("placeActionType") as PlaceActionType

        mapView = root.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SiteDetailsViewModel::class.java)
        adapter = PlaceCommentsAdapter(this.commentsRecyclerView)

        setupViews()

        place?.place_id?.let {
            if (placeActionType == PlaceActionType.VIEW) {
                viewModel.getPlace(it)
            } else {
                viewModel.getGooglePlace(it)
            }
        }

        viewModel.place.observe(this, Observer {
            place = it
            updateUI(it)
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            loading.visibility = if (isLoading) View.VISIBLE else View.GONE
            this.menu?.findItem(R.id.action_create_place)?.let {
                it.isEnabled = !isLoading
            }
        })

        this.createCommentButton.setOnClickListener {
            this.commentsText.clearFocus()
            this.commentsText.text.clear()

            val review = this.createReview()
            val place = this.place?.let {
                viewModel.createReview(it, review) { place ->
                    place?.let { updatedPlace ->
                        updateUI(updatedPlace)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        if (placeActionType == PlaceActionType.CREATE) {
            inflater.inflate(R.menu.button_menu, menu)
            menu.findItem(R.id.action_create_place).isEnabled = false
            this.menu = menu
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_create_place -> {
            this.place?.let {
                it.favourite = true

                val review = createReview()
                it.reviews?.add(review)

                viewModel.createPlace(it) {
                    val action = SiteDetailsFragmentDirections.actionSiteDetailsFragmentToNavigationDashboard()

                    findNavController().navigate(action.actionId)
                }
            }
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            this.findNavController().popBackStack()
            super.onOptionsItemSelected(item)

        }
    }

    override fun onMapReady(p0: GoogleMap) {
        // Add a marker in Sydney and move the camera
        map = p0

        val lat = this.place?.geometry?.location?.lat ?: return
        val lng = this.place?.geometry?.location?.lng ?: return

        val point = CameraUpdateFactory.newLatLng(LatLng(lat, lng))
        map.moveCamera(point)

        val location = LatLng(lat, lng)
        map.addMarker(MarkerOptions().position(location).title("Marker in ${this.place?.name}"))

        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(lat, lng)).zoom(15f).build()
        map.animateCamera(
            CameraUpdateFactory
                .newCameraPosition(cameraPosition)
        )

    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    private fun setupViews() {

        this.commentsRecyclerView.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(this.commentsRecyclerView.context, LinearLayoutManager.VERTICAL, false)
        this.commentsRecyclerView.layoutManager = linearLayoutManager
        commentsRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        this.createCommentButton.visibility = if (placeActionType == PlaceActionType.VIEW) View.VISIBLE else View.GONE
        this.commentsText.addTextChangedListener {
            this.menu?.findItem(R.id.action_create_place)?.let { item ->
                item.isEnabled = it?.isNotEmpty() ?: false
            }
        }
    }

    private fun updateUI(place: TOPlace) {
        placeNameTextView.text = place.name
        addressTextView.text = place.formatted_address
        phoneNumberTextView.text = "Phone number: ${place.formatted_phone_number ?: ""}"

        Picasso.get()
            .load(place.icon)
            .resize(30, 30)
            .into(placeIconImageView)

        this.adapter.run {
            reviews.clear()

            place.reviews?.let { reviews.addAll(it.sortedByDescending { review -> review.time }) }
            notifyDataSetChanged()
        }
    }

    private fun createReview(): Review {
        return Review(
            viewModel.user?.email!!,
            author_url = null,
            language = null,
            profile_photo_url = viewModel.user?.providerData?.first()?.photoUrl.toString(),
            rating = 0,
            relative_time_description = "A few moments ago",
            text = commentsText.text.toString(),
            time = Date().time
        )
    }
}
