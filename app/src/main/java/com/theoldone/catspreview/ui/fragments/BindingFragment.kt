package com.theoldone.catspreview.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import dagger.android.support.DaggerFragment

abstract class BindingFragment<T : ViewDataBinding>(private val layoutResId: Int) : DaggerFragment() {

	protected lateinit var binding: T

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, inState: Bundle?): View? {
		binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
		return binding.root
	}
}