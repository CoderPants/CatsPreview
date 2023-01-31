package com.theoldone.catspreview.ui.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentCatsBinding
import com.theoldone.catspreview.ui.adapters.CatsAdapter
import com.theoldone.catspreview.ui.decorations.MarginDecoration
import com.theoldone.catspreview.ui.screenstates.DismissProgress
import com.theoldone.catspreview.ui.screenstates.InitCats
import com.theoldone.catspreview.ui.screenstates.ShowBottomProgress
import com.theoldone.catspreview.ui.screenstates.ShowProgress
import com.theoldone.catspreview.utils.dp
import com.theoldone.catspreview.utils.launchMain
import com.theoldone.catspreview.vm.CatsVM
import javax.inject.Inject

class CatsFragment : BindingFragment<FragmentCatsBinding>(R.layout.fragment_cats), FavoritesController {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory
	lateinit var viewModel: CatsVM
	private val adapter by lazy { CatsAdapter(viewModel::loadNextPage, viewModel::onFavoriteClicked, viewModel::onDownloadClicked) }
	private var progressFadeAnimator: ValueAnimator? = null

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		//creating like this, because i need to trigger init in viewModel
		viewModel = viewModelProviderFactory.create(CatsVM::class.java)
		binding.rvCats.adapter = adapter
		binding.rvCats.addItemDecoration(MarginDecoration(middleIndentPx = 10.dp))
		//binding.btnNext.setOnSingleTap { findNavController().navigate(CatsFragmentDirections.actionCatsToFavoriteCats()) }
		observeUiState()
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
						ShowProgress -> binding.progress.show()
						DismissProgress -> hideProgress()
						ShowBottomProgress -> adapter.addBottomProgress()
					}
				}
		}
	}

	private fun hideProgress() {
		progressFadeAnimator?.cancel()
		progressFadeAnimator = ValueAnimator.ofFloat(1f, 0f).apply {
			addUpdateListener {
				binding.progress.alpha = it.animatedValue as Float
				binding.progressBackground.alpha = it.animatedValue as Float

			}
			duration = 400
			doOnEnd {
				binding.progress.visibility = View.GONE
				binding.progressBackground.visibility = View.GONE
			}
		}
		binding.progress.hide()
		progressFadeAnimator?.start()
	}
}