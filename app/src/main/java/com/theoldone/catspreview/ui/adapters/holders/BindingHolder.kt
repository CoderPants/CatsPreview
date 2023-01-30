package com.theoldone.catspreview.ui.adapters.holders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

open class BindingHolder<T : ViewDataBinding>(
	parent: ViewGroup,
	resId: Int,
	protected val binding: T = DataBindingUtil.inflate(LayoutInflater.from(parent.context), resId, parent, false),
) : BaseHolder(binding.root)