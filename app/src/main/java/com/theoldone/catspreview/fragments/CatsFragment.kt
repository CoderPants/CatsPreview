package com.theoldone.catspreview.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.theoldone.catspreview.CatsApplication
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentCatsBinding
import com.theoldone.catspreview.setOnSingleTap
import com.theoldone.catspreview.utils.RecourseManager
import javax.inject.Inject

class CatsFragment : BindingFragment<FragmentCatsBinding>(R.layout.fragment_cats) {
	@Inject
	lateinit var recourseManager: RecourseManager

	override fun onAttach(context: Context) {
		(context.applicationContext as CatsApplication).appComponent.inject(this)
		super.onAttach(context)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.btnNext.setOnSingleTap { findNavController().navigate(CatsFragmentDirections.actionCatsToFavoriteCats())}
		Log.v("MYTAG", "RecourseManager: $recourseManager")
	}
}