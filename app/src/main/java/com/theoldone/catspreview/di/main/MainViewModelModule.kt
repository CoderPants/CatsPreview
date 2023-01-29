package com.theoldone.catspreview.di.main

import androidx.lifecycle.ViewModel
import com.theoldone.catspreview.di.ViewModelKey
import com.theoldone.catspreview.viewmodels.CatsViewModel
import com.theoldone.catspreview.viewmodels.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindMainViewModel(mainViewModel: MainViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CatsViewModel::class)
    fun bindCatsViewModel(profileViewModel: CatsViewModel) : ViewModel
}