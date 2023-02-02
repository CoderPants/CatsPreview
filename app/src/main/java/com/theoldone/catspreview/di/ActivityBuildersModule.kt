package com.theoldone.catspreview.di

import com.theoldone.catspreview.di.main.MainFragmentBuildersModule
import com.theoldone.catspreview.di.main.MainViewModelModule
import com.theoldone.catspreview.ui.activities.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBuildersModule {
	@ContributesAndroidInjector(
		modules = [
			MainFragmentBuildersModule::class,
			MainViewModelModule::class
		]
	)
	fun contributeMainActivity(): MainActivity
}