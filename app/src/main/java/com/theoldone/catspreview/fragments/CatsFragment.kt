package com.theoldone.catspreview.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentCatsBinding
import com.theoldone.catspreview.setOnSingleTap
import com.theoldone.catspreview.viewmodels.CatsViewModel
import javax.inject.Inject

class CatsFragment : BindingFragment<FragmentCatsBinding>(R.layout.fragment_cats) {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory
	lateinit var viewModel: CatsViewModel

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		//creating like this, because i need to trigger init in viewModel
		viewModel = viewModelProviderFactory.create(CatsViewModel::class.java)
		binding.btnNext.setOnSingleTap { findNavController().navigate(CatsFragmentDirections.actionCatsToFavoriteCats()) }
	}
}