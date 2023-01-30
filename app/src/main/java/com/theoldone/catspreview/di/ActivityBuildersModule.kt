package com.theoldone.catspreview.di

import com.theoldone.catspreview.ui.activities.MainActivity
import com.theoldone.catspreview.di.main.MainFragmentBuildersModule
import com.theoldone.catspreview.di.main.MainModule
import com.theoldone.catspreview.di.main.MainScope
import com.theoldone.catspreview.di.main.MainViewModelModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ActivityBuildersModule {
	@MainScope
	@ContributesAndroidInjector(
		modules = [
			MainFragmentBuildersModule::class,
			MainModule::class,
			MainViewModelModule::class
		]
	)
	fun contributeMainActivity(): MainActivity
}