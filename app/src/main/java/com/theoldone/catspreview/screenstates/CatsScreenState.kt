package com.theoldone.catspreview.screenstates

import com.theoldone.catspreview.ui.viewmodels.CatViewModel

sealed class CatsScreenState
object ShowProgress : CatsScreenState()
object DismissProgress : CatsScreenState()
object ShowBottomProgress : CatsScreenState()
class Init(val cats: List<CatViewModel>) : CatsScreenState()