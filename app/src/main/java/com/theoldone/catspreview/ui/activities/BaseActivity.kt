package com.theoldone.catspreview.ui.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity<T : ViewDataBinding>(private val layoutResId: Int) : DaggerAppCompatActivity() {
	protected lateinit var binding: T

	override fun onCreate(inState: Bundle?) {
		super.onCreate(inState)
		binding = DataBindingUtil.setContentView(this, layoutResId)
	}
}