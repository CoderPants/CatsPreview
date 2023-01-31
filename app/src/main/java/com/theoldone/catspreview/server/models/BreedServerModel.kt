package com.theoldone.catspreview.server.models

import com.google.gson.annotations.SerializedName
import com.theoldone.catspreview.db.models.BreedDBModel

data class BreedServerModel(
	val id: String,
	val temperament: String,
	val origin: String,
	val country_code: String,
	val life_span: String,
	@SerializedName("wikipedia_url")
	val wikipediaUrl: String
)

fun BreedServerModel.toDBModel() = BreedDBModel(id, temperament, origin, country_code, life_span, wikipediaUrl)