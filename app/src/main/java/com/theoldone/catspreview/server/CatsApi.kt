package com.theoldone.catspreview.server

import com.theoldone.catspreview.server.models.CatModel
import retrofit2.http.GET
import retrofit2.http.Query

interface CatsApi {

	@GET("v1/images/search")
	suspend fun getCats(
		@Query("page") page: Int = 0,
		@Query("limit") limit: Int = CATS_PER_PAGE,
		@Query("order") order: String = "ASC", //ASC/DESC/RAND
		@Query("has_breeds") hasBreeds: Int = 1, //1 or 0 Only return images that have breed information
	): List<CatModel>

	companion object {
		private const val CATS_PER_PAGE = 12
	}
}