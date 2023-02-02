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
	val uiState by lazy { initFlow.asSharedFlow() }

	//Still thinks about this
	//Properly would be to save this into file and get it's path
	//but i don't have enough time for this
	var drawableToSave: Drawable? = null
	var catViewModelToSave: CatViewModel? = null
	private val initFlow = MutableSharedFlow<FavoritesScreenState>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
	private val catViewModelsCopy get() = catViewModels.map { it.copy() }
	private var catViewModels = mutableListOf<CatViewModel>()

	init {
		updateFavorites(favoriteCats)
	}

	fun updateFavorites(favoriteCats: List<CatDBModel>) {
		catViewModels = favoriteCats.map { it.toViewModel(true) }.toMutableList()
		viewModelScope.launchMain { initFlow.emit(InitFavorites(catViewModelsCopy)) }
	}

	fun onFavoriteClicked(catId: String) {
		if (catViewModels.removeIf { it.id == catId }) {
			viewModelScope.launch(Dispatchers.IO) { favoriteDao.deleteFavoriteCatById(catId) }
			viewModelScope.launchMain { initFlow.emit(InitFavorites(catViewModelsCopy)) }
		}
	}

	fun updateDownloadProgress(viewModel: CatViewModel, isDownloading: Boolean) {
		val catViewModel = catViewModels.find { it.id == viewModel.id } ?: return

		catViewModel.isDownloading = isDownloading
		viewModelScope.launchMain { initFlow.emit(InitFavorites(catViewModelsCopy)) }
	}
}