package com.theoldone.catspreview.di

import androidx.lifecycle.ViewModelProvider
import com.theoldone.catspreview.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelFactoryModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}