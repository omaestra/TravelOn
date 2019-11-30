package com.example.travelon.data.model

import com.example.travelon.data.model.Location
import com.example.travelon.data.model.Viewport
import java.io.Serializable

data class Geometry (
	val location : Location? = null,
	val viewport : Viewport? = null
): Serializable