package com.theoldone.catspreview.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentFavoriteCatsBinding
import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.di.main.favoriteCats.FavoriteCatsFactory
import com.theoldone.catspreview.ui.adapters.CatsAdapter
import com.theoldone.catspreview.ui.screenstates.InitFavorites
import com.theoldone.catspreview.utils.launchMain
import com.theoldone.catspreview.vm.FavoriteCatsVM
import javax.inject.Inject

class FavoriteCatsFragment : BindingFragment<FragmentFavoriteCatsBinding>(R.layout.fragment_favorite_cats), FavoritesConsumer {

	@Inject
	lateinit var factory: FavoriteCatsFactory
	lateinit var viewModel: FavoriteCatsVM
	private val adapter by lazy { CatsAdapter(viewModel::onFavoriteClicked, viewModel::onDownloadClicked) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val factory = factory.create((activity as? FavoritesProvider)?.favorites ?: emptyList())
		viewModel = factory.create(FavoriteCatsVM::class.java)
		observeUiState()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.rvFavoriteCats.adapter = adapter
	}

	override fun updateFavorites(favoriteCats: List<CatDBModel>) {
		viewModel.updateFavorites(favoriteCats)
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