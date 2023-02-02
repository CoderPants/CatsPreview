package com.theoldone.catspreview.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.theoldone.catspreview.R
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseFragment<T : ViewDataBinding>(private val layoutResId: Int) : DaggerFragment() {
	@Inject
	lateinit var settings: Settings
	protected lateinit var binding: T
	private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), this::handlePermissionResult)
	private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { handleSettingsResult() }
	private var savedCatViewModel: CatViewModel? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, inState: Bundle?): View? {
		binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.root.findViewById<Toolbar>(R.id.toolbar)?.setNavigationOnClickListener {
			if (activity?.isFinishing == false)
				activity?.onBackPressedDispatcher?.onBackPressed()
		}
	}

	protected open fun provideDrawable(catViewModel: CatViewModel): Drawable? = null

	protected open fun updateDownloadingProgress(catViewModel: CatViewModel, isDownloading: Boolean) {}

	protected fun onDownloadClicked(catViewModel: CatViewModel, drawable: Drawable) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
			when {
				userDeclinedWritePermission(settings) -> {
					savedCatViewModel = catViewModel
					showAlert()
				}
				requireContext().hasWritePermission() -> saveImageToDownloads(catViewModel, drawable)
				else -> {
					savedCatViewModel = catViewModel
					requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				}
			}
		} else {
			saveImageToDownloads(catViewModel, drawable)
		}
	}

	private fun showAlert() {
		val builder = AlertDialog.Builder(requireContext())
			.setTitle(R.string.alert_title)
			.setMessage(R.string.alert_message)
			.setNegativeButton(R.string.cancel) { _, _ -> savedCatViewModel = null }
			.setPositiveButton(R.string.go_to_settings) { _, _ -> settingsLauncher.launch(requireContext().createSettingsIntent()) }
		builder.show()
	}

	private fun handlePermissionResult(granted: Boolean) {
		if (granted) {
			saveImageToDownloads(savedCatViewModel ?: return, provideDrawable(savedCatViewModel ?: return))
		} else {
			savedCatViewModel = null
		}
	}

	private fun handleSettingsResult() {
		if (requireContext().hasWritePermission()) {
			saveImageToDownloads(savedCatViewModel ?: return, provideDrawable(savedCatViewModel ?: return))
		} else {
			savedCatViewModel = null
		}
	}

	private fun saveImageToDownloads(catViewModel: CatViewModel, drawable: Drawable?) {
		updateDownloadingProgress(catViewModel, isDownloading = true)
		saveImageToDownloadsInternal(catViewModel, drawable)
	}

	// don't like to download image inside fragment, but glide needs context
	@OptIn(DelicateCoroutinesApi::class)
	private fun saveImageToDownloadsInternal(catViewModel: CatViewModel, drawable: Drawable?) = GlobalScope.launch(Dispatchers.IO) {
		val context = requireContext().applicationContext
		//download image of it's original size if has internet
		val bitmap = if (hasInternetConnection(context)) {
			Glide.with(context)
				.asBitmap()
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.load(catViewModel.url)
				.awaitImage()
		} else {
			drawable?.toBitmap() ?: return@launch
		}

		val isSuccess = saveToDownloads(context, bitmap)
		launchMain {
			//"try catch" for case, when context would be cleared by system
			try {
				updateDownloadingProgress(catViewModel, isDownloading = false)
				Toast.makeText(context, if (isSuccess) R.string.file_saved_to_downloads else R.string.saving_error, Toast.LENGTH_SHORT).show()
				savedCatViewModel = null
			} catch (t: Throwable) {
				t.printStackTrace()
			}
		}
	}
}