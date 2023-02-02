package com.theoldone.catspreview.ui.adapters.holders

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import com.theoldone.catspreview.R
import com.theoldone.catspreview.databinding.HolderCatBinding
import com.theoldone.catspreview.ui.viewmodels.CatViewModel
import com.theoldone.catspreview.utils.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class CatHolder(
	parent: ViewGroup,
	private val onFavoriteClicked: (catId: String) -> Unit,
	private val onDownloadClicked: (CatViewModel, Drawable) -> Unit,
) : BindingHolder<HolderCatBinding>(parent, R.layout.holder_cat), ImageProvider {

	override val image: Drawable get() = binding.ivCat.drawable
	private val viewModel get() = item as CatViewModel
	private var waitingJob: Job? = null

	init {
		binding.btnDownload.setOnSingleTap {
			if (!viewModel.isDownloading)
				onDownloadClicked(viewModel, binding.ivCat.drawable)
		}
		binding.btnFavorite.setOnSingleTap { onFavoriteClicked(viewModel.id) }
	}

	override fun onRecycled() {
		super.onRecycled()
		waitingJob?.cancel()
	}

	override fun update(item: Any) {
		super.update(item)
		updateAll()
	}

	override fun update(item: Any, payloads: List<Any>) {
		super.update(item, payloads)
		if (payloads.contains(Payloads.FAVORITE))
			setTint(binding.btnFavorite, viewModel.favoriteColor)
		if (payloads.contains(Payloads.UPDATE_PROGRESS))
			updateProgress()
	}

	private fun updateAll() {
		updateProgress()
		binding.viewModel = viewModel
		binding.executePendingBindings()
	}

	@OptIn(DelicateCoroutinesApi::class)
	private fun updateProgress() {
		val update: () -> Unit = {
			setVisibility(binding.btnDownload, viewModel.hasDownload)
			setVisibility(binding.progressDownload, viewModel.isDownloading)
		}
		if (viewModel.isDownloading) {
			binding.progressDownload.show()
			update()
		} else {
			binding.progressDownload.hide()
			//wait for animation to finish
			waitingJob = GlobalScope.launchMain {
				delay(400)
				update()
			}
		}
	}
}