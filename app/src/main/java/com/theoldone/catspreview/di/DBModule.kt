package com.theoldone.catspreview.di

import android.app.Application
import androidx.room.Room
import com.theoldone.catspreview.db.CatsDataBase
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

	companion object {
		private const val DB_NAME = "com.theoldone.catspreview.db.CatsDataBase"
	}
}