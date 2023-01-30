package com.theoldone.catspreview.ui.adapters.holders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView

open class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
	var item: Any? = null
		private set

	constructor(resId: Int, parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(resId, parent, false))

	@CallSuper
	open fun update(item: Any) {
		this.item = item
	}

	@CallSuper
	open fun update(item: Any, payloads: List<Any>) {
		this.item = item
	}

	open fun onAttach() {}

	open fun onDetach() {}

	@CallSuper
	open fun onRecycled() {
	}
}