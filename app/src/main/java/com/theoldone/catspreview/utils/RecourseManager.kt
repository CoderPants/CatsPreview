package com.theoldone.catspreview.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

class RecourseManager(private val context: Context) {

	fun string(@StringRes stringRes: Int, vararg args: Any) = context.getString(stringRes, *args)

	fun color(@ColorRes colorRes: Int) = context.getColor(colorRes)
}