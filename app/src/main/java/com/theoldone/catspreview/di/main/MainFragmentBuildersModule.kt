package com.theoldone.catspreview.di.main

import com.theoldone.catspreview.di.main.cats.CatsViewModelModule
import com.theoldone.catspreview.di.main.cats.FavoriteViewModelModule
import com.theoldone.catspreview.ui.fragments.CatsFragment
import com.theoldone.catspreview.ui.fragments.FavoriteCatsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainFragmentBuildersModule {

	@ContributesAndroidInjector(modules = [CatsViewModelModule::class])
	fun contributeCatsFragment(): CatsFragment

	@ContributesAndroidInjector(modules = [FavoriteViewModelModule::class])
	fun contributeFavoriteCatsFragment(): FavoriteCatsFragment
}