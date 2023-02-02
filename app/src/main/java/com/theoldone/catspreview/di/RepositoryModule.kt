package com.theoldone.catspreview.di

import com.theoldone.catspreview.repositories.CatsRepository
import com.theoldone.catspreview.repositories.CatsRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [ServerModule::class, DBModule::class])
interface RepositoryModule {

	@Binds
	@Singleton
	fun bindCatsRepository(repository: CatsRepositoryImpl): CatsRepository
}