package com.example.travelon.ui.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelon.R
import com.example.travelon.data.model.Review
import com.example.travelon.data.model.TOPlace
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comment_row.view.*
import kotlinx.android.synthetic.main.place_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class PlaceCommentsAdapter(
    private val recyclerView: RecyclerView): RecyclerView.Adapter<PlaceCommentsAdapter.ViewHolder>() {

    val reviews = ArrayList<Review>()

    init {
        recyclerView.visibility = View.VISIBLE

        val layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = layoutManager
    }

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceCommentsAdapter.ViewHolder {
        val v = LayoutInflater.from(recyclerView.context)
            .inflate(R.layout.comment_row, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: PlaceCommentsAdapter.ViewHolder, position: Int) {
        val review = this.reviews[position]

        holder.bindItems(review)
    }

    override fun getItemCount(): Int {
        return reviews.count()
    }

    // The class is holding the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(review: Review) {

            itemView.authorNameTextView.text = review.author_name

            var time = review.time
            // Convert timestamp given in seconds to milliseconds.
            if (time < 1000000000000L) time *= 1000

            itemView.relativeTimeTextView.text = DateUtils.getRelativeTimeSpanString(time, Date().time, DateUtils.SECOND_IN_MILLIS)
            itemView.commentTextView.text = review.text

            review.profile_photo_url?.let {
                Picasso.get()
                    .load(review.profile_photo_url)
                    .resize(64, 64)
                    .into(itemView.authorImageView)
            }
        }
    }
}