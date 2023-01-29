package com.theoldone.catspreview.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerAppCompatActivity

abstract class BindingActivity<T : ViewDataBinding>(private val layoutResId: Int) : DaggerAppCompatActivity() {
	protected lateinit var binding: T

	override fun onCreate(inState: Bundle?) {
		super.onCreate(inState)
		binding = DataBindingUtil.setContentView(this, layoutResId)
	}
}