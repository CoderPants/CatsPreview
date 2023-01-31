package com.theoldone.catspreview.ui.screenstates

import com.theoldone.catspreview.ui.viewmodels.CatViewModel

sealed interface CatsScreenState
object ShowProgress : CatsScreenState
object DismissProgress : CatsScreenState
object ShowBottomProgress : CatsScreenState
class InitCats(val cats: List<CatViewModel>) : CatsScreenState