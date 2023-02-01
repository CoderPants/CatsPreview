package com.theoldone.catspreview.ui.screenstates

import com.theoldone.catspreview.ui.viewmodels.CatViewModel

sealed interface FavoritesScreenState
class InitFavorites(val cats: List<CatViewModel>): FavoritesScreenState