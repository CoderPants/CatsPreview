package com.theoldone.catspreview.ui.adapters

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.theoldone.catspreview.ui.adapters.holders.BaseHolder
import com.theoldone.catspreview.ui.adapters.holders.BottomProgressHolder
import com.theoldone.catspreview.ui.adapters.holders.CatHolder
import com.theoldone.catspreview.ui.viewmodels.BottomProgressViewModel
import com.theoldone.catspreview.ui.viewmodels.CatType
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.Payloads

class CatsAdapter(
	private val onFavoriteClicked: (catId: String) -> Unit,
	private val onDownloadClicked: (CatViewModel, Drawable) -> Unit,
	private val loadNextPage: (() -> Unit)? = null,
) : BaseListAdapter<CatType>(diffCallback) {

	override fun onViewAttachedToWindow(holder: BaseHolder) {
		super.onViewAttachedToWindow(holder)
		if (holder.bindingAdapterPosition == itemCount - 1)
			loadNextPage?.invoke()
	}

	override fun getItemViewType(position: Int) = when (currentList[position]) {
		is BottomProgressViewModel -> PROGRESS_TYPE
		is CatViewModel -> CAT_TYPE
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
		CAT_TYPE -> CatHolder(parent, onFavoriteClicked, onDownloadClicked)
		PROGRESS_TYPE -> BottomProgressHolder(parent)
		else -> throw IllegalArgumentException("There is no such view type! Type: $viewType")
	}

	fun addBottomProgress() {
		submitList(ArrayList(currentList).apply { add(BottomProgressViewModel()) })
	}

	companion object {
		private const val PROGRESS_TYPE = 0
		private const val CAT_TYPE = 1

		val diffCallback = object : DiffUtil.ItemCallback<CatType>() {
			override fun areItemsTheSame(oldItem: CatType, newItem: CatType) = oldItem.id == newItem.id

			override fun areContentsTheSame(oldItem: CatType, newItem: CatType) = oldItem == newItem

			override fun getChangePayload(oldItem: CatType, newItem: CatType) = when {
				oldItem !is CatViewModel || newItem !is CatViewModel -> null
				oldItem.isFavorite != newItem.isFavorite -> Payloads.FAVORITE
				oldItem.isDownloading != newItem.isDownloading -> Payloads.UPDATE_PROGRESS
				else -> null
			}
		}
	}
}