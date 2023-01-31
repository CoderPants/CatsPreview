package com.theoldone.catspreview.db.models

data class BreedDBModel(
	val id: String,
	val temperament: String,
	val origin: String,
	val country_code: String,
	val life_span: String,
	val wikipediaUrl: String
)