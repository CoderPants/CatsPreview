package com.theoldone.catspreview.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.theoldone.catspreview.db.models.CatDBModel

@Database(entities = [CatDBModel::class], version = 1)
abstract class CatsDataBase : RoomDatabase() {

	abstract fun launchDao(): FavoriteCatsDao

	companion object {
		private const val DB_NAME = "com.theoldone.catspreview.db.CatsDataBase"

		@Volatile
		private var INSTANCE: CatsDataBase? = null

		@Synchronized
		fun getDataBase(context: Context): CatsDataBase? {
			if (INSTANCE == null) {
				INSTANCE = Room
					.databaseBuilder(context.applicationContext, CatsDataBase::class.java, DB_NAME)
					.fallbackToDestructiveMigration()
					.build()
			}

			return INSTANCE
		}
	}
}