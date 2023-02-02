package com.theoldone.catspreview.ui.viewmodels

import androidx.annotation.ColorRes
import com.theoldone.catspreview.R
import com.theoldone.catspreview.db.models.CatDBModel

data class CatViewModel(
	override val id: String = "",
	val url: String = "",
	val imageWidth: Int = 0,
	val imageHeight: Int = 0,
	var isFavorite: Boolean = false,
	var isDownloading: Boolean = false
) : CatType {
	val hasDownload get() = !isDownloading

	@ColorRes
	val favoriteColor = if (isFavorite) R.color.razzmatazz else R.color.manatee
}

fun CatViewModel.toDBModel() = CatDBModel(id, url, imageWidth, imageHeight)