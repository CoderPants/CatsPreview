package com.theoldone.catspreview.di.main

import com.theoldone.catspreview.di.main.cats.CatsModule
import com.theoldone.catspreview.di.main.cats.CatsScope
import com.theoldone.catspreview.di.main.cats.CatsViewModelModule
import com.theoldone.catspreview.ui.fragments.CatsFragment
import com.theoldone.catspreview.ui.fragments.FavoriteCatsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainFragmentBuildersModule {

	@CatsScope
	@ContributesAndroidInjector(
		modules = [
			CatsModule::class,
			CatsViewModelModule::class
		]
	)
	fun contributeCatsFragment(): CatsFragment

	@ContributesAndroidInjector
	fun contributeFavoriteCatsFragment(): FavoriteCatsFragment
}