package com.theoldone.catspreview.ui.fragments

import com.theoldone.catspreview.db.models.CatDBModel

interface FavoritesProvider {
	val favorites: List<CatDBModel>
}