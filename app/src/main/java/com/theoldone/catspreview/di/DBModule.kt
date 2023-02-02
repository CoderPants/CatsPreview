package com.theoldone.catspreview.di

import android.app.Application
import androidx.room.Room
import com.theoldone.catspreview.db.CatsDataBase
import com.theoldone.catspreview.db.FavoriteCatsDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DBModule {
	@Provides
	@Singleton
	@Synchronized
	fun provideCatsDB(application: Application): CatsDataBase = Room
		.databaseBuilder(application.applicationContext, CatsDataBase::class.java, DB_NAME)
		.fallbackToDestructiveMigration()
		.build()

	@Provides
	fun provideFavoriteDao(dataBase: CatsDataBase): FavoriteCatsDao = dataBase.launchDao()

	companion object {
		private const val DB_NAME = "com.theoldone.catspreview.db.CatsDataBase"
	}
}