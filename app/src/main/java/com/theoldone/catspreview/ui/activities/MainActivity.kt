package com.theoldone.catspreview.ui.activities

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.ActvityMainBinding
import com.theoldone.catspreview.vm.MainVM
import javax.inject.Inject

class MainActivity : BaseActivity<ActvityMainBinding>(R.layout.actvity_main) {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory
	lateinit var viewModel: MainVM

	override fun onCreate(inState: Bundle?) {
		installSplashScreen()
		super.onCreate(inState)
		//creating like this, because i need to trigger init in viewModel
		viewModel = viewModelProviderFactory.create(MainVM::class.java)
	}
}