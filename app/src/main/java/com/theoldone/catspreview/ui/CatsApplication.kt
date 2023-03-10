package com.theoldone.catspreview.ui

import com.theoldone.catspreview.BuildConfig
import com.theoldone.catspreview.di.DaggerAppComponent
import dagger.android.support.DaggerApplication

class CatsApplication : DaggerApplication() {
	override fun applicationInjector() = DaggerAppComponent.builder()
		.application(this)
		.host(BuildConfig.HOST)
		.apiKey(BuildConfig.API_KEY)
		.build()
}