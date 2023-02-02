package com.theoldone.catspreview.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.ActvityMainBinding
import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.ui.fragments.FavoritesConsumer
import com.theoldone.catspreview.ui.fragments.FavoritesProvider
import com.theoldone.catspreview.ui.screenstates.UpdateFavorites
import com.theoldone.catspreview.utils.launchMain
import com.theoldone.catspreview.vm.MainVM
import javax.inject.Inject

class MainActivity : BaseActivity<ActvityMainBinding>(R.layout.actvity_main), FavoritesProvider {
	override val favorites: List<CatDBModel> get() = viewModel.favorites

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory
	lateinit var viewModel: MainVM
	private val currentFragment: Fragment?
		get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
			?.childFragmentManager
			?.fragments
			?.firstOrNull()

	override fun onCreate(inState: Bundle?) {
		installSplashScreen()
		super.onCreate(inState)
		//creating like this, because i need to trigger init in viewModel
		viewModel = viewModelProviderFactory.create(MainVM::class.java)
		observeUiState()
	}

	//For settings screen result
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		currentFragment?.onActivityResult(requestCode, resultCode, data)
	}

	private fun observeUiState() {
		lifecycleScope.launchMain {
			viewModel.uiState
				.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
				.collect { state ->
					if (state is UpdateFavorites)
						(currentFragment as? FavoritesConsumer)?.updateFavorites(state.favoritesCatIds)
				}
		}
	}
}