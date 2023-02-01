package com.theoldone.catspreview.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoldone.catspreview.db.FavoriteCatsDao
import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.ui.screenstates.MainScreenState
import com.theoldone.catspreview.ui.screenstates.UpdateFavorites
import com.theoldone.catspreview.utils.launchMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainVM @Inject constructor(favoriteCatsDao: FavoriteCatsDao) : ViewModel() {
	private val _uiState = MutableStateFlow<MainScreenState?>(null)
	val uiState = _uiState.asSharedFlow()
	var favorites: List<CatDBModel> = listOf()

	init {
		observeFavorite(favoriteCatsDao)
	}

	private fun observeFavorite(favoriteCatsDao: FavoriteCatsDao) = viewModelScope.launchMain {
		favoriteCatsDao.favoriteCats().flowOn(Dispatchers.IO).collect { cats ->
			favorites = cats
			_uiState.emit(UpdateFavorites(cats))
		}
	}
}