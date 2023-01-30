package com.theoldone.catspreview.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import com.theoldone.catspreview.utils.RecourseManager
import javax.inject.Inject

class MainVM @Inject constructor(private val recourseManager: RecourseManager) : ViewModel() {
	init {
		Log.v("MYTAG", "MainViewModel: $recourseManager")
	}
}