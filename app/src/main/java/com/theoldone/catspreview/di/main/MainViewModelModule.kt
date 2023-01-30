package com.theoldone.catspreview.di.main

import androidx.lifecycle.ViewModel
import com.theoldone.catspreview.di.ViewModelKey
import com.theoldone.catspreview.vm.MainVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainVM::class)
    fun bindMainViewModel(mainVM: MainVM) : ViewModel
}