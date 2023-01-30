package com.theoldone.catspreview.ui.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MarginDecoration(
	private val startIndentPx: Int = -1,
	private val middleIndentPx: Int = -1,
	private val endIndentPx: Int = -1
) : RecyclerView.ItemDecoration() {

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		val layoutManager = parent.layoutManager as? LinearLayoutManager ?: throw IllegalStateException("Supports only LinearLayoutManager")
		val adapter = parent.adapter ?: return
		val holder = parent.getChildViewHolder(view) ?: return
		if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) {
			outRect.setEmpty()
			return
		}

		when (holder.bindingAdapterPosition) {
			0 -> setStartingMargins(layoutManager, outRect)
			adapter.itemCount - 1 -> setEndingMargins(layoutManager, outRect)
			else -> outRect.bottom = middleIndentPx
		}
	}

	private fun setStartingMargins(layoutManager: LinearLayoutManager, outRect: Rect) {
		val letMiddleIndent = if(middleIndentPx > 0) middleIndentPx else 0
		if (layoutManager.reverseLayout) {
			if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
				outRect.bottom = startIndentPx
				outRect.top = letMiddleIndent
			} else {
				outRect.right = startIndentPx
				outRect.left = letMiddleIndent
			}
		} else {
			if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
				outRect.top = startIndentPx
				outRect.bottom = letMiddleIndent
			} else {
				outRect.left = startIndentPx
				outRect.right = letMiddleIndent
			}
		}
	}

	private fun setEndingMargins(layoutManager: LinearLayoutManager, outRect: Rect) {
		if (layoutManager.reverseLayout) {
			if (layoutManager.orientation == LinearLayoutManager.VERTICAL)
				outRect.top = endIndentPx
			else
				outRect.left = endIndentPx
		} else {
			if (layoutManager.orientation == LinearLayoutManager.VERTICAL)
				outRect.bottom = endIndentPx
			else
				outRect.right = endIndentPx
		}
	}
}