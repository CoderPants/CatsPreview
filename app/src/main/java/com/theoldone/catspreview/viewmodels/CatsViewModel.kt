package com.theoldone.catspreview.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.theoldone.catspreview.utils.RecourseManager
import retrofit2.Retrofit
import javax.inject.Inject

class CatsViewModel @Inject constructor(private val recourseManager: RecourseManager, retrofit: Retrofit) : ViewModel() {
	init {
		Log.v("MYTAG", "CatsViewModel: $recourseManager retrofit $retrofit")
	}
}