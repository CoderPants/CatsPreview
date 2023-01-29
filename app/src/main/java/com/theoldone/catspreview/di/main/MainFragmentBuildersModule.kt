package com.theoldone.catspreview.di.main

import com.theoldone.catspreview.fragments.CatsFragment
import com.theoldone.catspreview.fragments.FavoriteCatsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface MainFragmentBuildersModule {

	@ContributesAndroidInjector
	fun contributeCatsFragment(): CatsFragment

	@ContributesAndroidInjector
	fun contributeFavoriteCatsFragment(): FavoriteCatsFragment
}