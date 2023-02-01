package com.theoldone.catspreview.ui.screenstates

import com.theoldone.catspreview.ui.viewmodels.CatViewModel

sealed interface CatsScreenState
class UpdateProgress(val showProgress: Boolean) : CatsScreenState
object ShowBottomProgress : CatsScreenState
class InitCats(val cats: List<CatViewModel>) : CatsScreenState
class UpdateFavoriteText(val favoritesText: String) : CatsScreenState