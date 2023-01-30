package com.theoldone.catspreview.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.theoldone.catspreview.ui.adapters.holders.BaseHolder

abstract class BaseListAdapter<Model : Any>(itemCallback: DiffUtil.ItemCallback<Model>) : ListAdapter<Model, BaseHolder>(itemCallback) {
	override fun onBindViewHolder(holder: BaseHolder, position: Int, payloads: MutableList<Any>) {
		if (payloads.isNotEmpty())
			holder.update(getItem(position), payloads)
		else
			super.onBindViewHolder(holder, position, payloads)
	}

	override fun onBindViewHolder(holder: BaseHolder, position: Int) {
		holder.update(getItem(position))
	}

	override fun onViewAttachedToWindow(holder: BaseHolder) {
		super.onViewAttachedToWindow(holder)
		holder.onAttach()
	}

	override fun onViewDetachedFromWindow(holder: BaseHolder) {
		super.onViewDetachedFromWindow(holder)
		holder.onDetach()
	}

	override fun onViewRecycled(holder: BaseHolder) {
		super.onViewRecycled(holder)
		holder.onRecycled()
	}
}