package com.theoldone.catspreview.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.db.models.toViewModel
import com.theoldone.catspreview.repositories.CatsRepository
import com.theoldone.catspreview.ui.screenstates.FavoritesScreenState
import com.theoldone.catspreview.ui.screenstates.InitFavorites
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.launchMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteCatsVM @Inject constructor(private val repository: CatsRepository) : ViewModel() {
	private val initFlow = MutableSharedFlow<FavoritesScreenState>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
	private val catViewModelsCopy get() = catViewModels.map { it.copy() }
	private var catViewModels = mutableListOf<CatViewModel>()
	val uiState by lazy { initFlow.asSharedFlow() }

	init {
		viewModelScope.launchMain { subscribeToFavorites() }
	}

	private suspend fun subscribeToFavorites() {
		repository.favoriteCatsFlow.collect { list ->
			updateFavorites(list)
		}
	}

	private fun updateFavorites(favoriteCats: List<CatDBModel>) {
		catViewModels = favoriteCats.map { it.toViewModel(true) }.toMutableList()
		viewModelScope.launchMain { initFlow.emit(InitFavorites(catViewModelsCopy)) }
	}

	fun onFavoriteClicked(catId: String) {
		if (catViewModels.removeIf { it.id == catId }) {
			viewModelScope.launch(Dispatchers.IO) { repository.deleteFavoriteCat(catId) }
			viewModelScope.launchMain { initFlow.emit(InitFavorites(catViewModelsCopy)) }
		}
	}

	fun updateDownloadProgress(viewModel: CatViewModel, isDownloading: Boolean) {
		val catViewModel = catViewModels.find { it.id == viewModel.id } ?: return

		catViewModel.isDownloading = isDownloading
		viewModelScope.launchMain { initFlow.emit(InitFavorites(catViewModelsCopy)) }
	}
}