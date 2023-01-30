package com.theoldone.catspreview.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.theoldone.catspreview.ui.adapters.holders.CatHolder
import com.theoldone.catspreview.ui.viewmodels.CatViewModel

class CatsAdapter : BaseListAdapter<CatViewModel>(diffCallback) {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CatHolder(parent)

	companion object {
		val diffCallback = object : DiffUtil.ItemCallback<CatViewModel>() {
			override fun areItemsTheSame(oldItem: CatViewModel, newItem: CatViewModel) = oldItem.id == newItem.id

			override fun areContentsTheSame(oldItem: CatViewModel, newItem: CatViewModel) = oldItem == newItem
		}
	}
}