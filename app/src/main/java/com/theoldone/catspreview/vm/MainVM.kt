package com.theoldone.catspreview.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoldone.catspreview.repositories.CatsRepository
import com.theoldone.catspreview.utils.launchMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MainVM @Inject constructor(repository: CatsRepository) : ViewModel() {

	init {
		observeFavorite(repository)
	}

	private fun observeFavorite(repository: CatsRepository) = viewModelScope.launchMain {
		repository.favoriteCats().flowOn(Dispatchers.IO).collect { cats ->
			repository.emitFavorite(cats)
		}
	}
}