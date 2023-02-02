package com.theoldone.catspreview.ui.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentFavoriteCatsBinding
import com.theoldone.catspreview.ui.adapters.CatsAdapter
import com.theoldone.catspreview.ui.adapters.holders.ImageProvider
import com.theoldone.catspreview.ui.decorations.MarginDecoration
import com.theoldone.catspreview.ui.screenstates.InitFavorites
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.dp
import com.theoldone.catspreview.utils.launchMain
import com.theoldone.catspreview.vm.FavoriteCatsVM
import javax.inject.Inject

class FavoriteCatsFragment : BaseFragment<FragmentFavoriteCatsBinding>(R.layout.fragment_favorite_cats) {

	@Inject
	lateinit var factory: ViewModelProvider.Factory
	lateinit var viewModel: FavoriteCatsVM
	private val adapter by lazy { CatsAdapter(viewModel::onFavoriteClicked, this::onDownloadClicked) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = factory.create(FavoriteCatsVM::class.java)
		observeUiState()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY
		binding.rvFavoriteCats.adapter = adapter
		binding.rvFavoriteCats.addItemDecoration(MarginDecoration(middleIndentPx = 10.dp))
	}

	override fun updateDownloadingProgress(catViewModel: CatViewModel, isDownloading: Boolean) {
		viewModel.updateDownloadProgress(catViewModel, isDownloading)
	}

	override fun provideDrawable(catViewModel: CatViewModel): Drawable? {
		val holderIndex = adapter.currentList.indexOfFirst { it.id == catViewModel.id }.takeIf { it >= 0 } ?: return null

		return (binding.rvFavoriteCats.findViewHolderForAdapterPosition(holderIndex) as? ImageProvider)?.image
	}

	private fun observeUiState() {
		lifecycleScope.launchMain {
			viewModel.uiState
				.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
				.collect { state ->
					if (state is InitFavorites) {
						adapter.submitList(state.cats)
						binding.ivEmpty.visibility = if (state.cats.isEmpty()) VISIBLE else GONE
						binding.tvEmpty.visibility = if (state.cats.isEmpty()) VISIBLE else GONE
					}
				}
		}
	}
}