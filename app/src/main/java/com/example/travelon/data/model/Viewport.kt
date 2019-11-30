package com.example.travelon.data.model

import java.io.Serializable

data class Viewport (

	val northeast : Location? = null,
	val southwest : Location? = null
): Serializable