package com.theoldone.catspreview.ui.fragments

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
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
import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.ui.adapters.CatsAdapter
import com.theoldone.catspreview.ui.decorations.MarginDecoration
import com.theoldone.catspreview.ui.screenstates.InitCats
import com.theoldone.catspreview.ui.screenstates.ShowBottomProgress
import com.theoldone.catspreview.ui.screenstates.UpdateFavoriteText
import com.theoldone.catspreview.ui.screenstates.UpdateProgress
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.dp
import com.theoldone.catspreview.utils.launchMain
import com.theoldone.catspreview.utils.setOnSingleTap
import com.theoldone.catspreview.vm.CatsVM
import javax.inject.Inject

class CatsFragment : BaseFragment<FragmentCatsBinding>(R.layout.fragment_cats), FavoritesConsumer {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory
	lateinit var viewModel: CatsVM
	override val savedViewModel: CatViewModel? get() = viewModel.catViewModelToSave
	override val savedDrawable: Drawable? get() = viewModel.drawableToSave
	private val adapter by lazy { CatsAdapter(viewModel::onFavoriteClicked, this::onDownloadClicked, viewModel::loadNextPage) }
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
		updateFavorites((activity as? FavoritesProvider)?.favorites ?: emptyList())
	}

	override fun updateFavorites(favoriteCats: List<CatDBModel>) {
		viewModel.updateFavorites(favoriteCats.map { it.id })
	}

	override fun saveData(catViewModel: CatViewModel, drawable: Drawable) {
		viewModel.drawableToSave = drawable
		viewModel.catViewModelToSave = catViewModel
	}

	override fun updateDownloadingProgress(catViewModel: CatViewModel, isDownloading: Boolean) {
		viewModel.updateDownloadProgress(catViewModel, isDownloading)
	}

	private fun observeUiState() {
		lifecycleScope.launchMain {
			viewModel.uiState
				.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
				.collect { state ->
					when (state) {
						is InitCats -> {
							adapter.submitList(state.cats)
							binding.ivEmpty.visibility = if (state.cats.isEmpty()) VISIBLE else GONE
							binding.tvEmpty.visibility = if (state.cats.isEmpty()) VISIBLE else GONE
						}
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