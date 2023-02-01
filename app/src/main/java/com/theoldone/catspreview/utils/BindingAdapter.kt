package com.theoldone.catspreview.utils

import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.theoldone.catspreview.R

@BindingAdapter("bind:url", "bind:restrictSize", requireAll = false)
fun setImage(view: AppCompatImageView, url: String?, restrictSize: Boolean?) {
	if (url == null)
		return

	val downloadImage: () -> Unit = {
		var options = RequestOptions()
			.transform(RoundedCorners(20))
			.placeholder(R.drawable.image_place_holder)
			.diskCacheStrategy(DiskCacheStrategy.ALL)
		if (restrictSize == true)
			options = options.override(view.width, view.height)
		Glide
			.with(view.context)
			.load(url)
			.apply(options)
			//because cross fade has a bug
			.transition(GenericTransitionOptions.with(R.anim.fade_in_long))
			.into(view)
	}
	if (restrictSize == true && view.width == 0 && view.height == 0)
		view.post { downloadImage() }
	else
		downloadImage()
}

@BindingAdapter("bind:tintColor")
fun setTint(view: AppCompatImageView, @ColorRes colorId: Int) {
	view.drawable.setTintFixed(view.context.getColor(colorId))
}