package com.theoldone.catspreview.ui.screenstates

import com.theoldone.catspreview.ui.viewmodels.CatType

sealed interface CatsScreenState
class UpdateProgress(val showProgress: Boolean) : CatsScreenState
class InitCats(val cats: List<CatType>) : CatsScreenState
object FavoriteAnimation : CatsScreenState