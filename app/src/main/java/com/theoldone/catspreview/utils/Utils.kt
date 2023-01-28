package com.theoldone.catspreview

import android.view.View

fun View.setOnSingleTap(delay: Long = 200L, block: (v: View) -> Unit) {
	setOnClickListener {
		val lastClickedTime = getTag(R.string.single_tap_tag) as? Long
		if (lastClickedTime == null || System.currentTimeMillis() - lastClickedTime >= delay) {
			setTag(R.string.single_tap_tag, System.currentTimeMillis())
			block(this)
		}
	}
}