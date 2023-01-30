package com.theoldone.catspreview.di.main.cats

import androidx.lifecycle.ViewModel
import com.theoldone.catspreview.di.ViewModelKey
import com.theoldone.catspreview.vm.CatsVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CatsViewModelModule {
	@Binds
	@IntoMap
	@ViewModelKey(CatsVM::class)
	fun bindCatsViewModel(profileViewModel: CatsVM): ViewModel
}