package com.theoldone.catspreview

import androidx.multidex.MultiDexApplication
import com.theoldone.catspreview.di.AppComponent
import com.theoldone.catspreview.di.DaggerAppComponent

class CatsApplication : MultiDexApplication() {
	lateinit var appComponent: AppComponent
		private set

	override fun onCreate() {
		super.onCreate()
		appComponent = DaggerAppComponent.builder()
			.context(applicationContext)
			.build()
	}
}