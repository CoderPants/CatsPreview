package com.theoldone.catspreview.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.ActvityMainBinding
import com.theoldone.catspreview.viewmodels.MainViewModel
import javax.inject.Inject

class MainActivity : BindingActivity<ActvityMainBinding>(R.layout.actvity_main) {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory
	private val viewModel: MainViewModel by viewModels { viewModelProviderFactory }

	override fun onCreate(inState: Bundle?) {
		installSplashScreen()
		super.onCreate(inState)
		//viewModel
	}
}