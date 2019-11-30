package com.example.travelon.data.model

import java.io.Serializable

data class Photos (

	val height : Int? = null,
	val html_attributions : List<String>? = null,
	val photo_reference : String? = null,
	val width : Int? = null
): Serializable