package com.theoldone.catspreview.di.main

import com.theoldone.catspreview.db.CatsDataBase
import com.theoldone.catspreview.db.FavoriteCatsDao
import dagger.Module
import dagger.Provides

@Module
class MainModule {
	@Provides
	fun provideFavoriteDao(dataBase: CatsDataBase): FavoriteCatsDao = dataBase.launchDao()
}