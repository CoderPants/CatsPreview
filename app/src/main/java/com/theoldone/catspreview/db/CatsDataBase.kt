package com.theoldone.catspreview.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.theoldone.catspreview.db.models.CatDBModel

@Database(entities = [CatDBModel::class], version = 1)
abstract class CatsDataBase : RoomDatabase() {

	abstract fun launchDao(): FavoriteCatsDao
}