package com.theoldone.catspreview.di

import android.app.Application
import com.theoldone.catspreview.utils.Settings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

	@Provides
	@Singleton
	fun providesSettings(application: Application) = Settings(application)
}