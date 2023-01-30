package com.theoldone.catspreview.di

import androidx.lifecycle.ViewModelProvider
import com.theoldone.catspreview.vm.VMProviderFactory
import dagger.Binds
import dagger.Module

@Module
interface ViewModelFactoryModule {

    @Binds
    fun bindViewModelFactory(factory: VMProviderFactory): ViewModelProvider.Factory
}