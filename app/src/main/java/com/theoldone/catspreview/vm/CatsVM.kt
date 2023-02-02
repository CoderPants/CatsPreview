package com.theoldone.catspreview.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoldone.catspreview.repositories.CatsRepository
import com.theoldone.catspreview.server.models.toViewModel
import com.theoldone.catspreview.ui.screenstates.FavoriteAnimation
import com.theoldone.catspreview.ui.screenstates.InitCats
import com.theoldone.catspreview.ui.screenstates.UpdateProgress
import com.theoldone.catspreview.ui.viewmodels.BottomProgressViewModel
import com.theoldone.catspreview.ui.viewmodels.CatType
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.ui.viewmodels.toDBModel
import com.theoldone.catspreview.utils.Settings
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

class CatsVM @Inject constructor(
	private val repository: CatsRepository,
	private val settings: Settings
) : ViewModel() {
	private val initFlow = MutableSharedFlow<InitCats>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
	private val progressFlow = MutableSharedFlow<UpdateProgress>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
	private val updateFavoritesFlow = MutableSharedFlow<FavoriteAnimation>()
	private val catViewModels = mutableListOf<CatViewModel>()
	private val catViewModelsCopy get() = catViewModels.map { it.copy() }
	private var loadNextPageJob: Job? = null
	private var currentPage = 0
	private var favoritesIds = listOf<String>()
	val uiState by lazy { merge(initFlow, updateFavoritesFlow, progressFlow.asOneExecutionStrategy()) }

	init {
		viewModelScope.launchMain {
			progressFlow.emit(UpdateProgress(showProgress = true))
			loadCats()
			subscribeToFavorites()
		}
	}

	private suspend fun subscribeToFavorites() {
		repository.favoriteCatsFlow.collect { list ->
			updateFavorites(list.map { it.id })
		}
	}

	private fun updateFavorites(favoritesCatIds: List<String>) {
		favoritesIds = favoritesCatIds
		if (!settings.favoriteAnimPlayed && favoritesCatIds.isNotEmpty()) {
			settings.favoriteAnimPlayed = true
			settings.save()
			viewModelScope.launchMain { updateFavoritesFlow.emit(FavoriteAnimation) }
		}
		if (catViewModels.isEmpty())
			return

		catViewModels.forEach { catViewModel -> catViewModel.isFavorite = favoritesCatIds.any { catViewModel.id == it } }
		viewModelScope.launchMain { initFlow.emit(InitCats(catViewModelsCopy)) }
	}

	private suspend fun loadCats() {
		loadCatsInternal()
		progressFlow.emit(UpdateProgress(showProgress = false))
	}

	private suspend fun loadCatsInternal(loadNextPage: Boolean = false) {
		val loadedCats = try {
			withContext(IO) { repository.getCats(if (loadNextPage) currentPage + 1 else currentPage) }
		} catch (e: Exception) {
			e.printStackTrace()
			emptyList()
		}
		if (loadedCats.isNotEmpty() && loadNextPage)
			currentPage++
		catViewModels.addAll(loadedCats.map { model -> model.toViewModel(isFavorite = favoritesIds.any { model.id == it }) })
		initFlow.emit(InitCats(catViewModelsCopy))
	}

	fun loadNextPage() {
		if (loadNextPageJob?.isActive == true)
			return

		loadNextPageJob = viewModelScope.launchMain {
			val listWithBottomProgress = mutableListOf<CatType>().apply {
				addAll(catViewModelsCopy)
				add(BottomProgressViewModel())
			}
			initFlow.emit(InitCats(listWithBottomProgress))
			loadCatsInternal(loadNextPage = true)
		}
	}

	fun onFavoriteClicked(catId: String) {
		val catViewModel = catViewModels.find { it.id == catId } ?: return

		val catDBModel = catViewModel.toDBModel()
		catViewModel.isFavorite = !catViewModel.isFavorite
		viewModelScope.launch(IO) {
			if (catViewModel.isFavorite)
				repository.insertFavoriteCat(catDBModel)
			else
				repository.deleteFavoriteCat(catDBModel)
		}
		viewModelScope.launchMain { initFlow.emit(InitCats(catViewModelsCopy)) }
	}

	fun updateDownloadProgress(viewModel: CatViewModel, isDownloading: Boolean) {
		val catViewModel = catViewModels.find { it.id == viewModel.id } ?: return

		catViewModel.isDownloading = isDownloading
		viewModelScope.launchMain { initFlow.emit(InitCats(catViewModelsCopy)) }
	}
}