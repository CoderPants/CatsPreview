package com.theoldone.catspreview.fragments

import android.os.Bundle
import android.view.View
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentCatsBinding
import com.theoldone.catspreview.databinding.FragmentFavoriteCatsBinding
import com.theoldone.catspreview.setOnSingleTap

class FavoriteCatsFragment : BindingFragment<FragmentFavoriteCatsBinding>(R.layout.fragment_favorite_cats) {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

	}
}