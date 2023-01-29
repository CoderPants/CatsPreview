package com.theoldone.catspreview.di.main.cats

import com.theoldone.catspreview.server.CatsApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class CatsModule {
	@CatsScope
	@Provides
	fun provideAuthApi(retrofit: Retrofit): CatsApi = retrofit.create(CatsApi::class.java)
}