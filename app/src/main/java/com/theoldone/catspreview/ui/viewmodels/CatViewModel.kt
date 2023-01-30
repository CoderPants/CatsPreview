package com.theoldone.catspreview.ui.viewmodels

import com.theoldone.catspreview.server.models.CatModel

data class CatViewModel(
	val id: String = "",
	val url: String = "",
	val imageWidth: Int = 0,
	val imageHeight: Int = 0,
	val isFavorite: Boolean = false
)

fun CatModel.toViewModel(isFavorite: Boolean) = CatViewModel(id, url, imageWidth, imageHeight, isFavorite)