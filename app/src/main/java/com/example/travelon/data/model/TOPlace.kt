package com.example.travelon.data.model

import com.google.firebase.database.Exclude
import java.io.Serializable
import java.util.*

class TOPlace (
	val formatted_address : String = "",
	val geometry : Geometry? = null,
	val icon : String? = null,
	var id : String = "",
	val name : String = "",
	val photos : List<Photos>? = null,
	val place_id : String = "",
	val reference : String? = null,
	val types : List<String>? = null,
	var favourite: Boolean = false,
	var formatted_phone_number: String? = null,
	var reviews: ArrayList<Review>? = ArrayList(),
	var user: String? = null
): Serializable {
	@Exclude
	fun toMap(): Map<String, Any?> {
		return mapOf(
			"id" to id,
			"place_id" to place_id,
			"formatted_address" to formatted_address,
			"name" to name,
			"reference" to reference,
			"favourite" to favourite,
			"geometry" to geometry,
			"photos" to photos,
			"formatted_phone_number" to formatted_phone_number,
			"reviews" to reviews,
			"user" to user,
			"timestamp" to Date()
		)
	}
}