package com.theoldone.catspreview.activities

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.ActvityMainBinding

class MainActivity : BindingActivity<ActvityMainBinding>(R.layout.actvity_main) {

	override fun onCreate(inState: Bundle?) {
		installSplashScreen()
		super.onCreate(inState)
	}
}