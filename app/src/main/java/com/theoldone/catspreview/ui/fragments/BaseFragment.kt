package com.theoldone.catspreview.ui.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
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
	private var downloadIds: MutableList<Long> = mutableListOf()
	private var broadcastReceiver: BroadcastReceiver? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, inState: Bundle?): View? {
		binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
		broadcastReceiver = BroadcastReceiver { _, intent -> handleBroadCastReceiver(intent) }
		requireContext().registerReceiver(broadcastReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		savedCatViewModel = savedInstanceState?.getParcelableCompat(KEY_VIEW_MODEL)
		binding.root.findViewById<Toolbar>(R.id.toolbar)?.setNavigationOnClickListener {
			if (activity?.isFinishing == false)
				activity?.onBackPressedDispatcher?.onBackPressed()
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		requireContext().unregisterReceiver(broadcastReceiver)
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putParcelable(KEY_VIEW_MODEL, savedCatViewModel)
	}

	protected open fun provideDrawable(catViewModel: CatViewModel): Drawable? = null

	protected open fun updateDownloadingProgress(catViewModel: CatViewModel, isDownloading: Boolean) {}

	protected fun onDownloadClicked(catViewModel: CatViewModel, drawable: Drawable) {
		savedCatViewModel = catViewModel
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
			when {
				userDeclinedWritePermission(settings) -> showAlert()
				requireContext().hasWritePermission() -> saveImageToDownloads(catViewModel, drawable)
				else -> requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

	@OptIn(DelicateCoroutinesApi::class)
	private fun saveImageToDownloadsInternal(catViewModel: CatViewModel, drawable: Drawable?) = GlobalScope.launch(Dispatchers.IO) {
		val context = requireContext().applicationContext
		//if no internet, save predownloaded size
		if (!hasInternetConnection(context)) {
			val isSuccess = saveToDownloads(context, drawable?.toBitmap() ?: return@launch)

			launchMain { updateUiSafe(catViewModel, isSuccess) }
		} else {
			//download image of it's original size if has internet
			loadImageByUrl(catViewModel.url)
		}
	}

	private fun updateUiSafe(catViewModel: CatViewModel, showSuccessToast: Boolean) {
		//"try catch" for case, when context would be cleared by system
		try {
			updateDownloadingProgress(catViewModel, isDownloading = false)
			Toast.makeText(context, if (showSuccessToast) R.string.file_saved_to_downloads else R.string.saving_error, Toast.LENGTH_SHORT).show()
			savedCatViewModel = null
		} catch (t: Throwable) {
			t.printStackTrace()
		}
	}

	private suspend fun loadImageByUrl(url: String) {
		val context = requireContext().applicationContext
		val manager = context.getSystemService<DownloadManager>() ?: return

		val request = DownloadManager.Request(Uri.parse(url))
			.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, context.getString(R.string.cat))
			.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
			.setTitle(context.getString(R.string.app_name))
			.setDescription(context.getString(R.string.downloading))
			.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
			.setAllowedOverRoaming(false)
		val id = manager.enqueue(request)
		downloadIds.add(id)
		listenToDownloadManagerStatus(manager, id, this::handleOnFailure)
	}

	@OptIn(DelicateCoroutinesApi::class)
	private fun handleBroadCastReceiver(intent: Intent?) {
		if (intent?.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE)
			return

		val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
		if (downloadIds.contains(id)) {
			savedCatViewModel?.let { viewModel -> GlobalScope.launchMain { updateUiSafe(viewModel, showSuccessToast = true) } }
			downloadIds.remove(id)
		}
	}

	@OptIn(DelicateCoroutinesApi::class)
	private fun handleOnFailure(downloadId: Long) {
		if (downloadIds.contains(downloadId)) {
			savedCatViewModel?.let { viewModel -> GlobalScope.launchMain { updateUiSafe(viewModel, showSuccessToast = false) } }
			downloadIds.remove(downloadId)
		}
	}

	companion object {
		private const val KEY_VIEW_MODEL = "com.theoldone.catspreview.ui.fragments.KEY_VIEW_MODEL"
	}
}