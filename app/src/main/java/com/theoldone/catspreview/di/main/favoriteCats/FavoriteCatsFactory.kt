package com.theoldone.catspreview.di.main.favoriteCats

import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.vm.FavoriteCatsVMFactory
import dagger.assisted.AssistedFactory

@AssistedFactory
interface FavoriteCatsFactory {
	fun create(favoriteCats: List<CatDBModel>): FavoriteCatsVMFactory
}