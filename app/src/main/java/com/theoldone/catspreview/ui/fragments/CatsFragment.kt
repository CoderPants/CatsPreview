package com.theoldone.catspreview.ui.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentCatsBinding
import com.theoldone.catspreview.ui.adapters.CatsAdapter
import com.theoldone.catspreview.ui.decorations.MarginDecoration
import com.theoldone.catspreview.ui.screenstates.InitCats
import com.theoldone.catspreview.ui.screenstates.ShowBottomProgress
import com.theoldone.catspreview.ui.screenstates.UpdateFavoriteText
import com.theoldone.catspreview.ui.screenstates.UpdateProgress
import com.theoldone.catspreview.utils.dp
import com.theoldone.catspreview.utils.launchMain
import com.theoldone.catspreview.utils.setOnSingleTap
import com.theoldone.catspreview.vm.CatsVM
import javax.inject.Inject

class CatsFragment : BindingFragment<FragmentCatsBinding>(R.layout.fragment_cats), FavoritesController {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory
	lateinit var viewModel: CatsVM
	private val adapter by lazy { CatsAdapter(viewModel::loadNextPage, viewModel::onFavoriteClicked, viewModel::onDownloadClicked) }
	private var progressFadeAnimator: ValueAnimator? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		//creating like this, because i need to trigger init in viewModel
		viewModel = viewModelProviderFactory.create(CatsVM::class.java)
		observeUiState()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.rvCats.adapter = adapter
		binding.rvCats.addItemDecoration(MarginDecoration(middleIndentPx = 10.dp))
		binding.btnFavorites.setOnSingleTap { findNavController().navigate(CatsFragmentDirections.actionCatsToFavoriteCats()) }
	}

	override fun updateFavorites(favoritesCatIds: List<String>) {
		viewModel.updateFavorites(favoritesCatIds)
	}

	private fun observeUiState() {
		lifecycleScope.launchMain {
			viewModel.uiState
				.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
				.collect { state ->
					when (state) {
						is InitCats -> adapter.submitList(state.cats)
						is UpdateFavoriteText -> binding.tvFavoritesAmount.text = state.favoritesText
						is UpdateProgress -> updateProgress(state.showProgress)
						ShowBottomProgress -> adapter.addBottomProgress()
					}
				}
		}
	}

	private fun updateProgress(showProgress: Boolean) {
		val startAlpha = binding.progress.alpha
		val endAlpha = if (showProgress) 1f else 0f
		if (startAlpha == endAlpha)
			return

		progressFadeAnimator?.cancel()
		progressFadeAnimator = ValueAnimator.ofFloat(startAlpha, endAlpha).apply {
			addUpdateListener {
				binding.progress.alpha = it.animatedValue as Float
				binding.progressBackground.alpha = it.animatedValue as Float

			}
			duration = 600
			doOnStart {
				if (showProgress) {
					binding.progress.visibility = VISIBLE
					binding.progressBackground.visibility = VISIBLE
				}

			}
			doOnEnd {
				if (!showProgress) {
					binding.progress.visibility = GONE
					binding.progressBackground.visibility = GONE
				}
			}
		}
		if (showProgress) binding.progress.show() else binding.progress.hide()
		progressFadeAnimator?.start()
	}
}