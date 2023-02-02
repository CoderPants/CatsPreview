package com.theoldone.catspreview.ui.fragments

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.FragmentCatsBinding
import com.theoldone.catspreview.db.models.CatDBModel
import com.theoldone.catspreview.ui.adapters.CatsAdapter
import com.theoldone.catspreview.ui.decorations.MarginDecoration
import com.theoldone.catspreview.ui.screenstates.*
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.*
import com.theoldone.catspreview.vm.CatsVM
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class CatsFragment : BindingFragment<FragmentCatsBinding>(R.layout.fragment_cats), FavoritesConsumer {

	@Inject
	lateinit var viewModelProviderFactory: ViewModelProvider.Factory

	@Inject
	lateinit var settings: Settings
	lateinit var viewModel: CatsVM
	private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), this::handlePermissionResult)
	private val adapter by lazy { CatsAdapter(viewModel::onFavoriteClicked, this::onDownloadClicked, viewModel::loadNextPage) }
	private var progressFadeAnimator: ValueAnimator? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		//creating like this, because i need to trigger init in viewModel
		viewModel = viewModelProviderFactory.create(CatsVM::class.java)
		observeUiState()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.rvCats.adapter = adapter
		binding.rvCats.addItemDecoration(MarginDecoration(middleIndentPx = 10.dp))
		binding.btnFavorites.setOnSingleTap { findNavController().navigate(CatsFragmentDirections.actionCatsToFavoriteCats()) }
		updateFavorites((activity as? FavoritesProvider)?.favorites ?: emptyList())
	}

	//For settings screen
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == SETTINGS_REQUEST_CODE && requireContext().hasWritePermission()) {
			saveImageToDownloads()
		}
	}

	override fun updateFavorites(favoriteCats: List<CatDBModel>) {
		viewModel.updateFavorites(favoriteCats.map { it.id })
	}

	private fun observeUiState() {
		lifecycleScope.launchMain {
			viewModel.uiState
				.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
				.collect { state ->
					when (state) {
						is InitCats -> {
							adapter.submitList(state.cats)
							binding.ivEmpty.visibility = if (state.cats.isEmpty()) VISIBLE else GONE
							binding.tvEmpty.visibility = if (state.cats.isEmpty()) VISIBLE else GONE
						}
						is UpdateFavoriteText -> binding.tvFavoritesAmount.text = state.favoritesText
						is UpdateProgress -> updateProgress(state.showProgress)
						ShowBottomProgress -> adapter.addBottomProgress()
					}
				}
		}
	}

	private fun updateProgress(showProgress: Boolean) {
		val startAlpha = binding.progress.alpha
		val endAlpha = if (showProgress) 1f else 0f
		if (startAlpha == endAlpha)
			return

		progressFadeAnimator?.cancel()
		progressFadeAnimator = ValueAnimator.ofFloat(startAlpha, endAlpha).apply {
			addUpdateListener {
				binding.progress.alpha = it.animatedValue as Float
				binding.progressBackground.alpha = it.animatedValue as Float

			}
			duration = 600
			doOnStart {
				if (showProgress) {
					binding.progress.visibility = VISIBLE
					binding.progressBackground.visibility = VISIBLE
				}

			}
			doOnEnd {
				if (!showProgress) {
					binding.progress.visibility = GONE
					binding.progressBackground.visibility = GONE
				}
			}
		}
		if (showProgress) binding.progress.show() else binding.progress.hide()
		progressFadeAnimator?.start()
	}

	private fun onDownloadClicked(catViewModel: CatViewModel, drawable: Drawable) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
			when {
				userDeclinedWritePermission(settings) -> {
					viewModel.drawableToSave = drawable
					viewModel.catViewModelToUpdate = catViewModel
					showAlert()
				}
				requireContext().hasWritePermission() -> saveImageToDownloads(catViewModel, drawable)
				else -> {
					viewModel.drawableToSave = drawable
					viewModel.catViewModelToUpdate = catViewModel
					requestPermissionLauncher.launch(WRITE_EXTERNAL_STORAGE)
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
			.setNegativeButton(R.string.cancel, null)
			.setPositiveButton(R.string.go_to_settings) { _, _ ->
				requireActivity().openAppSettings(SETTINGS_REQUEST_CODE)
			}
		builder.show()
	}

	private fun handlePermissionResult(granted: Boolean) {
		if (granted)
			saveImageToDownloads()
	}

	private fun saveImageToDownloads(catViewModel: CatViewModel? = viewModel.catViewModelToUpdate, drawable: Drawable? = viewModel.drawableToSave) {
		viewModel.updateDownloadProgress(catViewModel ?: return, isDownloading = true)
		saveImageToDownloadsInternal(catViewModel, drawable ?: return)
	}

	// don't like to download image inside fragment, but glide needs context
	@OptIn(DelicateCoroutinesApi::class)
	private fun saveImageToDownloadsInternal(catViewModel: CatViewModel, drawable: Drawable) = GlobalScope.launch(Dispatchers.IO) {
		val context = requireContext().applicationContext
		//download image of it's original size if has internet
		val bitmap = if (hasInternetConnection(context)) {
			Glide.with(context)
				.asBitmap()
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.load(catViewModel.url)
				.awaitImage()
		} else {
			drawable.toBitmap()
		}
		val isSuccess = saveToDownloads(context, bitmap)
		launchMain {
			//"try catch" for case, when context would be cleared by system
			try {
				viewModel.updateDownloadProgress(catViewModel, isDownloading = false)
				Toast.makeText(context, if (isSuccess) R.string.file_saved_to_downloads else R.string.saving_error, Toast.LENGTH_SHORT).show()
			} catch (t: Throwable) {
				t.printStackTrace()
			}
		}
	}

	companion object {
		private const val SETTINGS_REQUEST_CODE = 11
	}
}