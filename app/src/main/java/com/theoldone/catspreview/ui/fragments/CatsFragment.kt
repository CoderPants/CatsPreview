package com.theoldone.catspreview.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentCatsBinding
import com.theoldone.catspreview.screenstates.*
import com.theoldone.catspreview.ui.adapters.CatsAdapter
import com.theoldone.catspreview.ui.decorations.MarginDecoration
import com.theoldone.catspreview.utils.dp
import com.theoldone.catspreview.vm.CatsVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CatsFragment : BindingFragment<FragmentCatsBinding>(R.layout.fragment_cats) {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory
	lateinit var viewModel: CatsVM
	private val adapter by lazy { CatsAdapter() }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		//creating like this, because i need to trigger init in viewModel
		viewModel = viewModelProviderFactory.create(CatsVM::class.java)
		binding.rvCats.adapter = adapter
		binding.rvCats.addItemDecoration(MarginDecoration(middleIndentPx = 10.dp, endIndentPx = 20.dp))
		//binding.btnNext.setOnSingleTap { findNavController().navigate(CatsFragmentDirections.actionCatsToFavoriteCats()) }
		observeUiState()
	}

	private fun observeUiState() {
		lifecycleScope.launch(Dispatchers.Main) {
			viewModel.uiState
				.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
				.collect { state ->
					when (state) {
						is Init -> adapter.submitList(state.cats)
						ShowProgress -> Unit//TODO()
						DismissProgress -> Unit//TODO()
						DismissPageProgress -> Unit//TODO()
						ShowPageProgress -> Unit//TODO()
					}
				}
		}
	}
}