package com.theoldone.catspreview.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class Settings(context: Context) {
	private val sharedPrefs: SharedPreferences
	var writePermissionDialogAppeared: Boolean = false

	init {
		sharedPrefs = context.getSharedPreferences(PERSISTENCE_NAME, Activity.MODE_PRIVATE)
		load()
	}

	private fun load() {
		writePermissionDialogAppeared = sharedPrefs.getBoolean(KEY_PERMISSION_DIALOG_APPEARED, writePermissionDialogAppeared)
	}

	fun save() {
		val editor = sharedPrefs.edit()
		editor.putBoolean(KEY_PERMISSION_DIALOG_APPEARED, writePermissionDialogAppeared)
		editor.apply()
	}

	companion object {
		private const val PERSISTENCE_NAME = "Settings"
		private const val KEY_PERMISSION_DIALOG_APPEARED = "permissionDialogAppeared"
	}
}