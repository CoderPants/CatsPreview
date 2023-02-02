package com.theoldone.catspreview.vm

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoldone.catspreview.db.FavoriteCatsDao
import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.db.models.toViewModel
import com.theoldone.catspreview.ui.screenstates.FavoritesScreenState
import com.theoldone.catspreview.ui.screenstates.InitFavorites
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.launchMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class FavoriteCatsVM(
	favoriteCats: List<CatDBModel>,
	private val favoriteDao: FavoriteCatsDao
) : ViewModel() {
	private val initFlow = MutableSharedFlow<FavoritesScreenState>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
	private val catViewModelsCopy get() = catsViewModels.map { it.copy() }
	private var catsViewModels = mutableListOf<CatViewModel>()
	val uiState = initFlow.asSharedFlow()

	init {
		updateFavorites(favoriteCats)
	}

	fun updateFavorites(favoriteCats: List<CatDBModel>) {
		catsViewModels = favoriteCats.map { it.toViewModel(true) }.toMutableList()
		viewModelScope.launchMain { initFlow.emit(InitFavorites(catViewModelsCopy)) }
	}

	fun onFavoriteClicked(catId: String) {
		if (catsViewModels.removeIf { it.id == catId }) {
			viewModelScope.launch(Dispatchers.IO) { favoriteDao.deleteFavoriteCatById(catId) }
			viewModelScope.launchMain { initFlow.emit(InitFavorites(catViewModelsCopy)) }
		}
	}

	fun onDownloadClicked(viewModel: CatViewModel, drawable: Drawable) {
		//todo
	}
}