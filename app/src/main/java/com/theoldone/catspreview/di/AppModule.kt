package com.theoldone.catspreview.di

import android.content.Context
import com.theoldone.catspreview.utils.RecourseManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
	@Provides
	@Singleton
	fun provideRecourseManager(context: Context) = RecourseManager(context)
}