package com.theoldone.catspreview.ui.activities

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.ActvityMainBinding
import com.theoldone.catspreview.ui.fragments.FavoritesController
import com.theoldone.catspreview.ui.screenstates.UpdateFavorites
import com.theoldone.catspreview.utils.RecourseManager
import com.theoldone.catspreview.utils.launchMain
import com.theoldone.catspreview.utils.setOnGroupSingleTap
import com.theoldone.catspreview.vm.MainVM
import javax.inject.Inject

class MainActivity : BindingActivity<ActvityMainBinding>(R.layout.actvity_main) {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory

	@Inject
	lateinit var recourseManager: RecourseManager
	lateinit var viewModel: MainVM
	private val activeColor by lazy { recourseManager.color(R.color.metallic_pink) }
	private val inactiveColor by lazy { recourseManager.color(R.color.han_blue) }
	private val activeTextColor by lazy { recourseManager.color(R.color.razzmatazz) }
	private val inactiveTextColor by lazy { recourseManager.color(R.color.white) }
	private val activeTextFont by lazy { recourseManager.font(R.font.nunito_black) }
	private val inactiveTextFont by lazy { recourseManager.font(R.font.nunito_semibold) }
	private val favoritesController: FavoritesController?
		get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
			?.childFragmentManager
			?.fragments
			?.firstOrNull() as? FavoritesController
	private val colorEvaluator = ArgbEvaluator()
	private var animationSet = AnimatorSet()

	override fun onCreate(inState: Bundle?) {
		installSplashScreen()
		super.onCreate(inState)
		viewModel = viewModelProviderFactory.create(MainVM::class.java)
		binding.btnCats.setOnGroupSingleTap(ANIM_DURATION, this::changeFragment)
		binding.btnFavorites.setOnGroupSingleTap(ANIM_DURATION, this::changeFragment)
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

	private fun changeFragment(view: View) {
		if (view.isSelected)
			return

		animationSet.cancel()
		animationSet = AnimatorSet() // because animator set remembers all previous anims, and there is no other way to delete them
		val btnCatsColorAnim = createColorAnimator(view.id, binding.btnCats)
		val btnFavoritesColorAnim = createColorAnimator(view.id, binding.btnFavorites)
		animationSet.playTogether(btnCatsColorAnim, btnFavoritesColorAnim)
		animationSet.start()
	}

	private fun createColorAnimator(viewId: Int, button: AppCompatTextView) = ValueAnimator.ofFloat(0f, 1f).apply {
		button.isSelected = viewId == button.id
		button.typeface = if (button.isSelected) activeTextFont else inactiveTextFont
		val startBackgroundColor = if (button.isSelected) inactiveColor else activeColor
		val endBackgroundColor = if (button.isSelected) activeColor else inactiveColor
		val startTextColor = if (button.isSelected) inactiveTextColor else activeTextColor
		val endTextColor = if (button.isSelected) activeTextColor else inactiveTextColor
		addUpdateListener {
			ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(colorEvaluator.evaluate(animatedFraction, startBackgroundColor, endBackgroundColor) as Int))
			button.setTextColor(colorEvaluator.evaluate(animatedFraction, startTextColor, endTextColor) as Int)
		}
		doOnEnd {
			ViewCompat.setBackgroundTintList(button, ColorStateList.valueOf(endBackgroundColor))
			button.setTextColor(endTextColor)
		}
		duration = ANIM_DURATION
	}

	companion object {
		private const val ANIM_DURATION = 400L
	}
}