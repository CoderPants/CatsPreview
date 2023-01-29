package com.theoldone.catspreview.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.theoldone.catspreview.utils.RecourseManager
import javax.inject.Inject

class CatsViewModel @Inject constructor(private val recourseManager: RecourseManager) : ViewModel() {
	init {
		Log.v("MYTAG", "CatsViewModel: $recourseManager")
	}
}