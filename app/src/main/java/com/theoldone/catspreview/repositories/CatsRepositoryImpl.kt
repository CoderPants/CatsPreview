package com.theoldone.catspreview.repositories

import com.theoldone.catspreview.db.FavoriteCatsDao
import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.server.CatsApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class CatsRepositoryImpl @Inject constructor(
	private val catsApi: CatsApi,
	private val favoriteCatsDao: FavoriteCatsDao,
) : CatsRepository {

	private val _favoriteCatsFlow = MutableStateFlow<List<CatDBModel>>(emptyList())
	override val favoriteCatsFlow: StateFlow<List<CatDBModel>> = _favoriteCatsFlow.asStateFlow()

	override suspend fun insertFavoriteCat(cat: CatDBModel) {
		favoriteCatsDao.insertFavoriteCat(cat)
	}

	override suspend fun deleteFavoriteCat(cat: CatDBModel) {
		favoriteCatsDao.deleteFavoriteCat(cat)
	}

	override suspend fun deleteFavoriteCat(catId: String) {
		favoriteCatsDao.deleteFavoriteCat(catId)
	}

	override fun favoriteCats(): Flow<List<CatDBModel>> = favoriteCatsDao.favoriteCats()

	override suspend fun emitFavorite(favoriteCats: List<CatDBModel>) {
		_favoriteCatsFlow.emit(favoriteCats)
	}

	override suspend fun getCats(page: Int, limit: Int, order: String, hasBreeds: Int) = catsApi.getCats(page, limit, order, hasBreeds)
}