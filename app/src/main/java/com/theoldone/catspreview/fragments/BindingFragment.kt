package com.theoldone.catspreview.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BindingFragment<T : ViewDataBinding>(private val layoutResId: Int) : androidx.fragment.app.Fragment() {

	protected lateinit var binding: T

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, inState: Bundle?): View? {
		binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
		return binding.root
	}
}