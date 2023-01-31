package com.theoldone.catspreview.ui.adapters.holders

import android.view.ViewGroup
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.HolderCatBinding
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.setOnSingleTap

class CatHolder(parent: ViewGroup) : BindingHolder<HolderCatBinding>(parent, R.layout.holder_cat) {
	init {
		binding.btnDownload.setOnSingleTap { }
		binding.btnFavorite.setOnSingleTap {}
	}

	override fun update(item: Any) {
		super.update(item)
		updateAll(item)
	}

	override fun update(item: Any, payloads: List<Any>) {
		super.update(item, payloads)
		updateAll(item)
	}

	private fun updateAll(item: Any) {
		val viewModel = item as CatViewModel
		if (binding.ivCat.width == 0 && binding.ivCat.height == 0)
			binding.ivCat.post { initImage(viewModel.url) }
		else
			initImage(viewModel.url)
	}

	private fun initImage(url: String) {
		val options = RequestOptions()
			.transform(RoundedCorners(20))
			.placeholder(R.drawable.image_place_holder)
			.diskCacheStrategy(DiskCacheStrategy.ALL)
			.override(binding.ivCat.width, binding.ivCat.height)
		Glide
			.with(itemView.context)
			.load(url)
			.apply(options)
			//because cross fade has a bug
			.transition(GenericTransitionOptions.with(R.anim.fade_in_long))
			.into(binding.ivCat)
	}
}