package com.theoldone.catspreview.ui.screenstates

import com.theoldone.catspreview.db.models.CatDBModel

sealed interface MainScreenState
class UpdateFavorites(val favoritesCatIds: List<CatDBModel>): MainScreenState