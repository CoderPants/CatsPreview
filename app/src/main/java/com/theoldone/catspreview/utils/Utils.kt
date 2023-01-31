package com.theoldone.catspreview.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import com.theoldone.catspreview.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun CoroutineScope.launchMain(block: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.Main, block = block)

fun View.setOnSingleTap(delay: Long = 200L, block: (v: View) -> Unit) {
	setOnClickListener {
		val lastClickedTime = getTag(R.string.single_tap_tag) as? Long
		if (lastClickedTime == null || System.currentTimeMillis() - lastClickedTime >= delay) {
			setTag(R.string.single_tap_tag, System.currentTimeMillis())
			block(this)
		}
	}
}

fun View.setOnGroupSingleTap(delay: Long = 200L, block: (v: View) -> Unit) {
	setOnClickListener {
		val root = context.asActivity()?.window?.decorView ?: return@setOnClickListener

		val lastClickedTime = root.getTag(R.string.single_group_tag) as? Long
		if (lastClickedTime == null || System.currentTimeMillis() - lastClickedTime >= delay) {
			root.setTag(R.string.single_group_tag, System.currentTimeMillis())
			block(this)
		}
	}
}

fun Context.asActivity(): AppCompatActivity? {
	var curWrapper = this
	while (curWrapper is ContextWrapper) {
		if (curWrapper is AppCompatActivity)
			return curWrapper
		curWrapper = curWrapper.baseContext
	}
	return null
}

//Sometimes drawable setTint doesn't work
fun Drawable.setTintFixed(color: Int) {
	val wrapped = DrawableCompat.wrap(this).mutate()
	DrawableCompat.setTint(wrapped, color)
}

val Int.dp get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Float.dp get() = this * Resources.getSystem().displayMetrics.density