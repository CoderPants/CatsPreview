package com.theoldone.catspreview.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BindingActivity<T : ViewDataBinding>(private val layoutResId: Int) : AppCompatActivity() {
	protected lateinit var binding: T

	override fun onCreate(inState: Bundle?) {
		super.onCreate(inState)
		binding = DataBindingUtil.setContentView(this, layoutResId)
	}
}