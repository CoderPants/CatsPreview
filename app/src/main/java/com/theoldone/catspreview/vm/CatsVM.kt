package com.theoldone.catspreview.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoldone.catspreview.screenstates.CatsScreenState
import com.theoldone.catspreview.screenstates.Init
import com.theoldone.catspreview.screenstates.ShowProgress
import com.theoldone.catspreview.server.CatsApi
import com.theoldone.catspreview.ui.viewmodels.toViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CatsVM @Inject constructor(private val catsApi: CatsApi) : ViewModel() {
	private val _uiState = MutableStateFlow<CatsScreenState>(ShowProgress)
	val uiState = _uiState.asStateFlow()
	private var currentPage = 0

	init {
		loadCats()
	}

	private fun loadCats() = viewModelScope.launch(Main) {
		val cats = withContext(IO) { catsApi.getCats(currentPage) }
		Log.v("MYTAG", "catsIds: ${cats.map { it.id }}")
		Log.v("MYTAG", "catsIds: ${cats.map { it.url }}")
		/*Log.v("MYTAG", "Cats size: ${cats.size}")
		cats.forEach {
			Log.v("MYTAG", "Id ${it.id} breed: ${it.breeds}")
		}*/
		_uiState.emit(Init(cats.map { it.toViewModel(isFavorite = false) }))
	}
}