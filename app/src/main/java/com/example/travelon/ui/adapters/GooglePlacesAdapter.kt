package com.example.travelon.ui.adapters

import TOPlace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelon.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.favourite_place_row.view.*
import kotlinx.android.synthetic.main.place_row.view.*
import kotlinx.android.synthetic.main.place_row.view.createPlaceButton
import kotlinx.android.synthetic.main.place_row.view.placeCityText
import kotlinx.android.synthetic.main.place_row.view.placeCountryText
import kotlinx.android.synthetic.main.place_row.view.placeImageView
import kotlinx.android.synthetic.main.place_row.view.placeNameText

class GooglePlacesAdapter(
    private val recyclerView: RecyclerView,
    private val onClickListener: SelectPlaceClickListener,
    private val onCreatePlaceClickListener: SelectPlaceClickListener?): RecyclerView.Adapter<GooglePlacesAdapter.ViewHolder>() {

    val places = ArrayList<TOPlace>()

    init {
        recyclerView.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = layoutManager
    }

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(recyclerView.context).inflate(R.layout.favourite_place_row, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = this.places[position]

        holder.bindItems(place)

        holder.itemView.setOnClickListener { view ->
            onClickListener(view, place)
        }

        holder.itemView.createPlaceButton.setOnClickListener { view ->
            onCreatePlaceClickListener?.invoke(view, place)
        }
    }

    override fun getItemCount(): Int {
        return places.count()
    }

    // The class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ApiKey = "AIzaSyBla0a-Mh6b3Vr2h8z9vl3__Cpq6rzGCsY"

        fun bindItems(place: TOPlace) {

            itemView.placeNameText.text = place.name
            itemView.placeCityText.text = place.formatted_address
            itemView.placeCountryText.text = place.formatted_address

            place.photos?.first()?.photo_reference.let {
                var imageUrl =
                    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photoreference=${it}&key=$ApiKey"

                Picasso.get()
                    .load(imageUrl)
                    .resize(50, 50)
                    .into(itemView.placeImageView)
            }

        }
    }
}