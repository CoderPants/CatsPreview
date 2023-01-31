package com.theoldone.catspreview.ui.screenstates

sealed interface MainScreenState
class UpdateFavorites(val favoritesCatIds: List<String>): MainScreenState