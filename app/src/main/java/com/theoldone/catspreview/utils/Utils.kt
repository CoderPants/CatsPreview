package com.theoldone.catspreview.utils

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import com.theoldone.catspreview.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

val Int.dp get() = (this * Resources.getSystem().displayMetrics.density).toInt()

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

//Sometimes drawable setTint doesn't work
fun Drawable.setTintFixed(color: Int) {
	val wrapped = DrawableCompat.wrap(this).mutate()
	DrawableCompat.setTint(wrapped, color)
}

//I need to remember data till view is not active
//after getting data, i need to delete all data in replay cache, so view, after recreation, won't trigger it one more time
@OptIn(ExperimentalCoroutinesApi::class)
fun <T : Any> MutableSharedFlow<T>.asOneExecutionStrategy() = onEach { this.resetReplayCache() }