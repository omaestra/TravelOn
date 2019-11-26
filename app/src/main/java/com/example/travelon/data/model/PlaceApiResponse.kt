package com.example.travelon.data.model

import TOPlace

data class PlaceApiResponse (

	val html_attributions : List<String>,
	val results : List<TOPlace>,
	val status : String
)