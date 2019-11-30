package com.example.travelon.ui.adapters

import com.example.travelon.data.model.TOPlace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelon.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.place_row.view.*

typealias SelectPlaceClickListener = (View, TOPlace) -> Unit

class PlacesAdapter(
    private val recyclerView: RecyclerView,
    private val onClickListener: SelectPlaceClickListener,
    private val onFavouriteClickListener: SelectPlaceClickListener?): RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {
    //private val mContext: Context = context

    val places = ArrayList<TOPlace>()

    init {
        recyclerView.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = layoutManager
    }

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesAdapter.ViewHolder {
        val v = LayoutInflater.from(recyclerView.context).inflate(R.layout.place_row, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: PlacesAdapter.ViewHolder, position: Int) {
        val place = this.places[position]

        holder.bindItems(place)

        holder.itemView.setOnClickListener { view ->
            onClickListener(view, place)
        }

        holder.itemView.button_favorite.setOnClickListener { view ->
            onFavouriteClickListener?.invoke(view, place)
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
            itemView.button_favorite.isChecked = place.favourite

            place.photos?.first()?.photo_reference.let {
                var imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=100&photoreference=${it}&key=$ApiKey"

                Picasso.get()
                    .load(imageUrl)
                    .resize(50, 50)
                    .into(itemView.placeImageView)
            }

        }
    }
}