package com.theoldone.catspreview.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theoldone.catspreview.db.FavoriteCatsDao
import com.theoldone.catspreview.db.models.CatDBModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class FavoriteCatsVMFactory @AssistedInject constructor(
	@Assisted private val favoriteCats: List<CatDBModel>,
	private val favoriteDao: FavoriteCatsDao
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T = FavoriteCatsVM(favoriteCats, favoriteDao) as T
}