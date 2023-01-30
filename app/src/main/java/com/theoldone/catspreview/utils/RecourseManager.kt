package com.theoldone.catspreview.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import javax.inject.Inject

class RecourseManager @Inject constructor(private val context: Context) {

	fun string(@StringRes stringId: Int, vararg args: Any) = context.getString(stringId, *args)

	fun color(@ColorRes colorId: Int) = context.getColor(colorId)

	fun font(@FontRes fontId: Int) = ResourcesCompat.getFont(context, fontId)
}