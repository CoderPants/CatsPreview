package com.theoldone.catspreview.di.main.cats

import androidx.lifecycle.ViewModel
import com.theoldone.catspreview.di.ViewModelKey
import com.theoldone.catspreview.viewmodels.CatsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CatsViewModelModule {
	@Binds
	@IntoMap
	@ViewModelKey(CatsViewModel::class)
	fun bindCatsViewModel(profileViewModel: CatsViewModel): ViewModel
}