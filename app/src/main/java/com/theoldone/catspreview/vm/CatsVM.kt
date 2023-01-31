package com.theoldone.catspreview.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoldone.catspreview.screenstates.CatsScreenState
import com.theoldone.catspreview.screenstates.DismissProgress
import com.theoldone.catspreview.screenstates.Init
import com.theoldone.catspreview.server.CatsApi
import com.theoldone.catspreview.ui.viewmodels.toViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CatsVM @Inject constructor(private val catsApi: CatsApi) : ViewModel() {
	private val _uiState = MutableSharedFlow<CatsScreenState>(10, 0, BufferOverflow.SUSPEND)
	private var currentPage = 0
	val uiState = _uiState.asSharedFlow()

	init {
		loadCats()
	}

	private fun loadCats() = viewModelScope.launch(Main) {
		val cats = withContext(IO) { catsApi.getCats(currentPage) }
		_uiState.emit(Init(cats.map { it.toViewModel(isFavorite = false) }))
		_uiState.emit(DismissProgress)
	}
}