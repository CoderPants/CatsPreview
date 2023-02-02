package com.theoldone.catspreview.repositories

import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.server.CatsApi
import com.theoldone.catspreview.server.models.CatServerModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface CatsRepository {
	val favoriteCatsFlow: StateFlow<List<CatDBModel>>

	suspend fun insertFavoriteCat(cat: CatDBModel)

	suspend fun deleteFavoriteCat(cat: CatDBModel)

	suspend fun deleteFavoriteCat(catId: String)

	fun favoriteCats(): Flow<List<CatDBModel>>

	suspend fun emitFavorite(favoriteCats: List<CatDBModel>)

	suspend fun getCats(page: Int = 0, limit: Int = CatsApi.CATS_PER_PAGE, order: String = "ASC", hasBreeds: Int = 1): List<CatServerModel>
}