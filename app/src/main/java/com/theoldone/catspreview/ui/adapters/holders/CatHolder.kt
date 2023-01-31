package com.theoldone.catspreview.ui.adapters.holders

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.HolderCatBinding
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.Payloads
import com.theoldone.catspreview.utils.setOnSingleTap
import com.theoldone.catspreview.utils.setTint

class CatHolder(
	parent: ViewGroup,
	private val onFavoriteClicked: (catId: String) -> Unit,
	private val onDownloadClicked: (CatViewModel, Drawable) -> Unit,
) : BindingHolder<HolderCatBinding>(parent, R.layout.holder_cat) {

	private val viewModel get() = item as CatViewModel

	init {
		binding.btnDownload.setOnSingleTap { onDownloadClicked(viewModel, binding.ivCat.drawable) }
		binding.btnFavorite.setOnSingleTap { onFavoriteClicked(viewModel.id) }
	}

	override fun update(item: Any) {
		super.update(item)
		updateAll()
	}

	override fun update(item: Any, payloads: List<Any>) {
		super.update(item, payloads)
		if (payloads.contains(Payloads.FAVORITE))
			setTint(binding.btnFavorite, viewModel.favoriteColor)
	}

	private fun updateAll() {
		binding.viewModel = viewModel
		binding.executePendingBindings()
	}
}