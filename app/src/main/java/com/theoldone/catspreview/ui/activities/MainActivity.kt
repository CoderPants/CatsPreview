package com.theoldone.catspreview.ui.activities

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.ActvityMainBinding
import com.theoldone.catspreview.ui.fragments.FavoritesController
import com.theoldone.catspreview.ui.screenstates.UpdateFavorites
import com.theoldone.catspreview.utils.launchMain
import com.theoldone.catspreview.vm.MainVM
import javax.inject.Inject

class MainActivity : BindingActivity<ActvityMainBinding>(R.layout.actvity_main) {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory
	lateinit var viewModel: MainVM
	private val favoritesController: FavoritesController?
		get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
			?.childFragmentManager
			?.fragments
			?.firstOrNull() as? FavoritesController

	override fun onCreate(inState: Bundle?) {
		installSplashScreen()
		super.onCreate(inState)
		//creating like this, because i need to trigger init in viewModel
		viewModel = viewModelProviderFactory.create(MainVM::class.java)
		observeUiState()
	}

	private fun observeUiState() {
		lifecycleScope.launchMain {
			viewModel.uiState
				.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
				.collect { state ->
					if (state is UpdateFavorites)
						favoritesController?.updateFavorites(state.favoritesCatIds)
				}
		}
	}
}