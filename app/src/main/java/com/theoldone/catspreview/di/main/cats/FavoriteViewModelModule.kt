package com.theoldone.catspreview.di.main.cats

import androidx.lifecycle.ViewModel
import com.theoldone.catspreview.di.ViewModelKey
import com.theoldone.catspreview.vm.FavoriteCatsVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface FavoriteViewModelModule {

	@Binds
	@IntoMap
	@ViewModelKey(FavoriteCatsVM::class)
	fun bindFavoriteCatsViewModel(catsVM: FavoriteCatsVM): ViewModel
}