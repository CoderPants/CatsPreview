package com.theoldone.catspreview.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.theoldone.catspreview.db.models.CatDBModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCatsDao {
	@Insert(onConflict = REPLACE)
	suspend fun insertFavoriteCat(cat: CatDBModel)

	@Delete
	suspend fun deleteFavoriteCat(cat: CatDBModel)

	@Query("DELETE FROM $FAVORITE_TABLE_NAME WHERE id = :catId")
	suspend fun deleteFavoriteCat(catId: String)

	@Query("SELECT * FROM $FAVORITE_TABLE_NAME")
	fun favoriteCats(): Flow<List<CatDBModel>>

	companion object {
		const val FAVORITE_TABLE_NAME = "FAVORITE_TABLE"
	}
}