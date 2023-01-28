package com.theoldone.catspreview.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentCatsBinding
import com.theoldone.catspreview.setOnSingleTap

class CatsFragment : BindingFragment<FragmentCatsBinding>(R.layout.fragment_cats) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.btnNext.setOnSingleTap { findNavController().navigate(CatsFragmentDirections.actionCatsToFavoriteCats())}
	}
}