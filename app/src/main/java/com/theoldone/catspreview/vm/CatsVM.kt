package com.theoldone.catspreview.vm

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoldone.catspreview.db.FavoriteCatsDao
import com.theoldone.catspreview.server.CatsApi
import com.theoldone.catspreview.server.models.CatServerModel
import com.theoldone.catspreview.server.models.toDBModel
import com.theoldone.catspreview.server.models.toViewModel
import com.theoldone.catspreview.ui.screenstates.CatsScreenState
import com.theoldone.catspreview.ui.screenstates.DismissProgress
import com.theoldone.catspreview.ui.screenstates.InitCats
import com.theoldone.catspreview.ui.screenstates.ShowBottomProgress
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.launchMain
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CatsVM @Inject constructor(private val catsApi: CatsApi, private val favoriteCatsDao: FavoriteCatsDao) : ViewModel() {
	//Amount of CastScreenState offsprings == 4
	private val _uiState = MutableSharedFlow<CatsScreenState>(4, 0, BufferOverflow.SUSPEND)
	private val catViewModels = mutableListOf<CatViewModel>()
	private val catModels = mutableListOf<CatServerModel>()
	private val catViewModelsCopy get() = catViewModels.map { it.copy() }
	private var loadNextPageJob: Job? = null
	private var currentPage = 0
	private var favoritesIds = listOf<String>()
	val uiState = _uiState.asSharedFlow()

	init {
		viewModelScope.launchMain { loadCats() }
	}

	private suspend fun loadCats() {
		loadCatsInternal()
		_uiState.emit(DismissProgress)
	}

	private suspend fun loadCatsInternal() {
		val loadedCats = withContext(IO) { catsApi.getCats(currentPage) }
		catModels.addAll(loadedCats)
		val newViewModels = loadedCats.map { model -> model.toViewModel(isFavorite = favoritesIds.any { model.id == it }) }
		catViewModels.addAll(newViewModels)
		_uiState.emit(InitCats(catViewModelsCopy))
	}

	fun loadNextPage() {
		if (loadNextPageJob?.isActive == true)
			return

		loadNextPageJob = viewModelScope.launchMain {
			_uiState.emit(ShowBottomProgress)
			currentPage++
			loadCatsInternal()
		}
	}

	fun onFavoriteClicked(catId: String) {
		val catViewModel = catViewModels.find { it.id == catId } ?: return
		val catDBModel = catModels.find { it.id == catId }?.toDBModel() ?: return

		catViewModel.isFavorite = !catViewModel.isFavorite
		viewModelScope.launch(IO) {
			if (catViewModel.isFavorite)
				favoriteCatsDao.insertFavoriteCat(catDBModel)
			else
				favoriteCatsDao.deleteFavoriteCat(catDBModel)
		}
		viewModelScope.launchMain { _uiState.emit(InitCats(catViewModelsCopy)) }
	}

	fun onDownloadClicked(catViewModel: CatViewModel, drawable: Drawable) {
		//todo
	}

	fun updateFavorites(favoritesCatIds: List<String>) {
		favoritesIds = favoritesCatIds
		if (catViewModels.isEmpty())
			return

		catViewModels.forEach { catViewModel -> catViewModel.isFavorite = favoritesCatIds.any { catViewModel.id == it } }
		viewModelScope.launchMain { _uiState.emit(InitCats(catViewModelsCopy)) }
	}
}