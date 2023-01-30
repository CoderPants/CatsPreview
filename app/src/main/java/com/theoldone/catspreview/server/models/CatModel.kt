package com.theoldone.catspreview.server.models

import com.google.gson.annotations.SerializedName

data class CatModel(
	val id: String = "",
	val url: String = "",
	@SerializedName("width")
	val imageWidth: Int = 0,
	@SerializedName("height")
	val imageHeight: Int = 0,
	val breeds: List<Breed>
)

data class Breed(
	val id: String,
	val temperament: String,
	val origin: String,
	val country_code: String,
	val life_span: String,
	@SerializedName("wikipedia_url")
	val wikipediaUrl: String
)
