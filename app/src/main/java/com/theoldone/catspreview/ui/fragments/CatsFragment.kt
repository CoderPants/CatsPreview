package com.theoldone.catspreview.ui.fragments

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentCatsBinding
import com.theoldone.catspreview.ui.adapters.CatsAdapter
import com.theoldone.catspreview.ui.adapters.holders.ImageProvider
import com.theoldone.catspreview.ui.decorations.MarginDecoration
import com.theoldone.catspreview.ui.screenstates.FavoriteAnimation
import com.theoldone.catspreview.ui.screenstates.InitCats
import com.theoldone.catspreview.ui.screenstates.UpdateProgress
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.dp
import com.theoldone.catspreview.utils.launchMain
import com.theoldone.catspreview.utils.onLastItemCallback
import com.theoldone.catspreview.utils.setOnSingleTap
import com.theoldone.catspreview.vm.CatsVM
import javax.inject.Inject

class CatsFragment : BaseFragment<FragmentCatsBinding>(R.layout.fragment_cats) {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory
	lateinit var viewModel: CatsVM
	private val adapter by lazy { CatsAdapter(viewModel::onFavoriteClicked, this::onDownloadClicked) }
	private var progressFadeAnimator: ValueAnimator? = null
	private var pulsingAnimator: ValueAnimator? = null
	private var listener: RecyclerView.OnChildAttachStateChangeListener? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		//creating like this, because i need to trigger init in viewModel
		viewModel = viewModelProviderFactory.create(CatsVM::class.java)
		observeUiState()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY
		binding.rvCats.adapter = adapter
		binding.rvCats.addItemDecoration(MarginDecoration(middleIndentPx = 10.dp))
		listener = binding.rvCats.onLastItemCallback(viewModel::loadNextPage)
		binding.btnFavorites.setOnSingleTap { onFavoriteClick() }
	}

	override fun onDestroyView() {
		super.onDestroyView()
		listener?.let { binding.rvCats.removeOnChildAttachStateChangeListener(it) }
	}

	override fun provideDrawable(catViewModel: CatViewModel): Drawable? {
		val holderIndex = adapter.currentList.indexOfFirst { it.id == catViewModel.id }.takeIf { it >= 0 } ?: return null

		return (binding.rvCats.findViewHolderForAdapterPosition(holderIndex) as? ImageProvider)?.image
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
						is FavoriteAnimation -> startPulsingAnim()
						is UpdateProgress -> updateProgress(state.showProgress)
					}
				}
		}
	}

	private fun onFavoriteClick() {
		pulsingAnimator?.cancel()
		findNavController().navigate(CatsFragmentDirections.actionCatsToFavoriteCats())
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

	private fun startPulsingAnim() {
		pulsingAnimator?.cancel()
		pulsingAnimator = ValueAnimator.ofFloat(0.92f, 1.22f).apply {
			addUpdateListener {
				binding.btnFavorites.scaleX = animatedValue as Float
				binding.btnFavorites.scaleY = animatedValue as Float
			}
			duration = 800
			repeatCount = Animation.INFINITE
			repeatMode = ValueAnimator.REVERSE
			doOnCancel {
				binding.btnFavorites.scaleX = 1f
				binding.btnFavorites.scaleY = 1f
			}
		}
		pulsingAnimator?.start()
	}
}