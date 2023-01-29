package com.theoldone.catspreview

import com.theoldone.catspreview.di.DaggerAppComponent
import dagger.android.support.DaggerApplication

class CatsApplication : DaggerApplication() {
	override fun applicationInjector() = DaggerAppComponent.builder().application(this).build()
}