package com.theoldone.catspreview.utils

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DownloadManager
import android.content.*
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.theoldone.catspreview.R
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import java.io.*


private val contentValues: ContentValues
	get() = ContentValues().apply {
		put(MediaStore.Images.Media.MIME_TYPE, "image/png")
		put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
	}

val Int.dp get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun CoroutineScope.launchMain(block: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.Main, block = block)

fun View.setOnSingleTap(delay: Long = 200L, block: (v: View) -> Unit) {
	setOnClickListener {
		val lastClickedTime = getTag(R.string.single_tap_tag) as? Long
		if (lastClickedTime == null || System.currentTimeMillis() - lastClickedTime >= delay) {
			setTag(R.string.single_tap_tag, System.currentTimeMillis())
			block(this)
		}
	}
}

//Sometimes drawable setTint doesn't work
fun Drawable.setTintFixed(color: Int) {
	val wrapped = DrawableCompat.wrap(this).mutate()
	DrawableCompat.setTint(wrapped, color)
}

fun RecyclerView.onLastItemCallback(block: () -> Unit): RecyclerView.OnChildAttachStateChangeListener {
	val listener = object : RecyclerView.OnChildAttachStateChangeListener {
		override fun onChildViewAttachedToWindow(view: View) {
			val size = adapter?.itemCount ?: return

			if (getChildViewHolder(view)?.bindingAdapterPosition == size - 1)
				block.invoke()
		}

		override fun onChildViewDetachedFromWindow(view: View) {}
	}
	addOnChildAttachStateChangeListener(listener)
	return listener
}

//I need to remember data till view is not active
//after getting data, i need to delete all data in replay cache, so view, after recreation, won't trigger it one more time
@OptIn(ExperimentalCoroutinesApi::class)
fun <T : Any> MutableSharedFlow<T>.asOneExecutionStrategy() = onEach { this.resetReplayCache() }

suspend fun saveToDownloads(context: Context, bitmap: Bitmap) =
	if (Build.VERSION.SDK_INT >= 29) saveToDownloadsFromQ(context, bitmap) else saveToDownloadsLessThenQ(context, bitmap)

@RequiresApi(Build.VERSION_CODES.Q)
private suspend fun saveToDownloadsFromQ(context: Context, bitmap: Bitmap): Boolean = withContext(Dispatchers.IO) {
	val values = contentValues
	values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
	values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
	//To give access for other apps to see our image
	values.put(MediaStore.Images.Media.IS_PENDING, false)
	val contentResolver = context.contentResolver
	val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return@withContext false
	val out = outputStream(contentResolver, uri) ?: return@withContext false

	if (!bitmap.compress(Bitmap.CompressFormat.PNG, 90, out))
		return@withContext false

	contentResolver.update(uri, values, null, null)
	return@withContext true
}

private suspend fun saveToDownloadsLessThenQ(context: Context, bitmap: Bitmap): Boolean = withContext(Dispatchers.IO) {
	val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString())
	if (!directory.exists())
		directory.mkdirs()

	val outFile = File(directory, System.currentTimeMillis().toString() + ".png")
	val out = outputStream(outFile) ?: return@withContext false

	if (!bitmap.compress(Bitmap.CompressFormat.PNG, 90, out))
		return@withContext false

	val values = contentValues
	values.put(MediaStore.Images.Media.DATA, outFile.absolutePath)
	context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
	return@withContext true
}

private fun outputStream(contentResolver: ContentResolver, uri: Uri) = try {
	contentResolver.openOutputStream(uri)
} catch (e: FileNotFoundException) {
	e.printStackTrace()
	null
}

private fun outputStream(file: File) = try {
	file.outputStream()
} catch (e: FileNotFoundException) {
	e.printStackTrace()
	null
}

fun Context.hasWritePermission() = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED

fun Fragment.userDeclinedWritePermission(settings: Settings): Boolean {
	val shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)
	if (shouldShowRequestPermissionRationale && !settings.writePermissionDialogAppeared) {
		settings.writePermissionDialogAppeared = true
		settings.save()
	}
	return !requireContext().hasWritePermission() && !shouldShowRequestPermissionRationale && settings.writePermissionDialogAppeared
}

fun Context.createSettingsIntent() = Intent().apply {
	action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
	addCategory(Intent.CATEGORY_DEFAULT)
	data = Uri.parse("package:${packageName}")
}

fun hasInternetConnection(context: Context): Boolean {
	val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	val network = connectivityManager.activeNetwork ?: return false
	val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
	return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
		capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
		capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}

fun BroadcastReceiver(block: (context: Context?, intent: Intent?) -> Unit) = object : BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		block.invoke(context, intent)
	}
}

inline fun <reified T : Parcelable> Bundle?.getParcelableCompat(key: String) = when {
	Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> this?.getParcelable(key, T::class.java)
	else -> @Suppress("DEPRECATION") this?.getParcelable(key) as? T
}


suspend fun listenToDownloadManagerStatus(downloadManager: DownloadManager, downloadID: Long, onFailure: (downloadID: Long) -> Unit) {
	while (true) {
		val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
		if (cursor.moveToFirst()) {
			val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS).takeIf { it >= 0 } ?: continue
			when (cursor.getInt(columnIndex)) {
				DownloadManager.STATUS_FAILED -> {
					onFailure(downloadID)
					break
				}
				DownloadManager.STATUS_SUCCESSFUL -> break
			}
		}
		cursor.close()
		delay(500)
	}
}