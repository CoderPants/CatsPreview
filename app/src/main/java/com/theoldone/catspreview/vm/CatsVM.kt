package com.theoldone.catspreview.vm

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoldone.catspreview.R
import com.theoldone.catspreview.db.FavoriteCatsDao
import com.theoldone.catspreview.server.CatsApi
import com.theoldone.catspreview.server.models.CatServerModel
import com.theoldone.catspreview.server.models.toDBModel
import com.theoldone.catspreview.server.models.toViewModel
import com.theoldone.catspreview.ui.screenstates.InitCats
import com.theoldone.catspreview.ui.screenstates.ShowBottomProgress
import com.theoldone.catspreview.ui.screenstates.UpdateFavoriteText
import com.theoldone.catspreview.ui.screenstates.UpdateProgress
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.RecourseManager
import com.theoldone.catspreview.utils.asOneExecutionStrategy
import com.theoldone.catspreview.utils.launchMain
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CatsVM @Inject constructor(private val catsApi: CatsApi, private val favoriteCatsDao: FavoriteCatsDao, private var recourseManager: RecourseManager) : ViewModel() {
	private val initFlow = MutableSharedFlow<InitCats>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
	private val updateFavoritesFlow = MutableSharedFlow<UpdateFavoriteText>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
	private val progressFlow = MutableSharedFlow<UpdateProgress>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
	private val showBottomProgressFlow = MutableSharedFlow<ShowBottomProgress>()
	val uiState by lazy { merge(initFlow, updateFavoritesFlow, progressFlow.asOneExecutionStrategy(), showBottomProgressFlow) }
	private val catViewModels = mutableListOf<CatViewModel>()
	private val catModels = mutableListOf<CatServerModel>()
	private val catViewModelsCopy get() = catViewModels.map { it.copy() }
	private var loadNextPageJob: Job? = null
	private var currentPage = 0
	private var favoritesIds = listOf<String>()

	init {
		viewModelScope.launchMain {
			progressFlow.emit(UpdateProgress(showProgress = true))
			loadCats()
		}
	}

	private suspend fun loadCats() {
		loadCatsInternal()
		progressFlow.emit(UpdateProgress(showProgress = false))
	}

	private suspend fun loadCatsInternal() {
		val loadedCats = withContext(IO) { catsApi.getCats(currentPage) }
		catModels.addAll(loadedCats)
		val newViewModels = loadedCats.map { model -> model.toViewModel(isFavorite = favoritesIds.any { model.id == it }) }
		catViewModels.addAll(newViewModels)
		initFlow.emit(InitCats(catViewModelsCopy))
	}

	fun loadNextPage() {
		if (loadNextPageJob?.isActive == true)
			return

		loadNextPageJob = viewModelScope.launchMain {
			showBottomProgressFlow.emit(ShowBottomProgress)
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
		viewModelScope.launchMain { initFlow.emit(InitCats(catViewModelsCopy)) }
	}

	fun onDownloadClicked(catViewModel: CatViewModel, drawable: Drawable) {
		//todo
	}

	fun updateFavorites(favoritesCatIds: List<String>) {
		favoritesIds = favoritesCatIds
		viewModelScope.launchMain { updateFavoritesFlow.emit(UpdateFavoriteText(recourseManager.string(R.string.favorites_amount, favoritesCatIds.size))) }
		if (catViewModels.isEmpty())
			return

		catViewModels.forEach { catViewModel -> catViewModel.isFavorite = favoritesCatIds.any { catViewModel.id == it } }
		viewModelScope.launchMain { initFlow.emit(InitCats(catViewModelsCopy)) }
	}
}