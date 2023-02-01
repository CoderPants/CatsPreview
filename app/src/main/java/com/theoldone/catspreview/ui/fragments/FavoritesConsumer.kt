package com.theoldone.catspreview.ui.fragments

import com.theoldone.catspreview.db.models.CatDBModel

interface FavoritesConsumer {
	fun updateFavorites(favoriteCats: List<CatDBModel>)
}