package com.theoldone.catspreview.server.models

import com.google.gson.annotations.SerializedName
import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.ui.viewmodels.CatViewModel

data class CatServerModel(
	val id: String = "",
	val url: String = "",
	@SerializedName("width")
	val imageWidth: Int = 0,
	@SerializedName("height")
	val imageHeight: Int = 0,
	val breeds: List<BreedServerModel>
)

fun CatServerModel.toViewModel(isFavorite: Boolean) = CatViewModel(id, url, imageWidth, imageHeight, isFavorite)

fun CatServerModel.toDBModel() = CatDBModel(id, url, imageWidth, imageHeight, breeds.map { it.toDBModel() })