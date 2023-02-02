package com.theoldone.catspreview.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theoldone.catspreview.db.FavoriteCatsDao
import com.theoldone.catspreview.ui.viewmodels.CatViewModel

@Entity(tableName = FavoriteCatsDao.FAVORITE_TABLE_NAME)
data class CatDBModel(
	@PrimaryKey
	val id: String = "",
	val url: String = "",
	val imageWidth: Int = 0,
	val imageHeight: Int = 0
)

fun CatDBModel.toViewModel(isFavorite: Boolean) = CatViewModel(id, url, imageWidth, imageHeight, isFavorite)