package com.example.travelon.ui.adapters

import android.content.Context
import android.content.Intent
import android.text.style.CharacterStyle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelon.R
import com.example.travelon.data.model.TOPlace
import kotlinx.android.synthetic.main.place_row.view.*

typealias SelectPlaceClickListener = (View, TOPlace) -> Unit

class PlacesAdapter(private val recyclerView: RecyclerView, private val onClickListener: SelectPlaceClickListener): RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {
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
    }

    override fun getItemCount(): Int {
        return places.count()
    }

    // The class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(place: TOPlace) {

            itemView.placeTextView.text = place.name

        }
    }
}