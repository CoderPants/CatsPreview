package com.theoldone.catspreview.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorRes
import com.theoldone.catspreview.R
import com.theoldone.catspreview.db.models.CatDBModel

data class CatViewModel(
	override val id: String = "",
	val url: String = "",
	val imageWidth: Int = 0,
	val imageHeight: Int = 0,
	var isFavorite: Boolean = false,
	var isDownloading: Boolean = false
) : CatType, Parcelable {
	val hasDownload get() = !isDownloading

	@ColorRes
	val favoriteColor = if (isFavorite) R.color.razzmatazz else R.color.manatee

	constructor(parcel: Parcel) : this(
		parcel.readString() ?: "",
		parcel.readString() ?: "",
		parcel.readInt(),
		parcel.readInt(),
		parcel.readByte() != 0.toByte(),
		parcel.readByte() != 0.toByte()
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(id)
		parcel.writeString(url)
		parcel.writeInt(imageWidth)
		parcel.writeInt(imageHeight)
		parcel.writeByte(if (isFavorite) 1 else 0)
		parcel.writeByte(if (isDownloading) 1 else 0)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<CatViewModel> {
		override fun createFromParcel(parcel: Parcel): CatViewModel {
			return CatViewModel(parcel)
		}

		override fun newArray(size: Int): Array<CatViewModel?> {
			return arrayOfNulls(size)
		}
	}
}

fun CatViewModel.toDBModel() = CatDBModel(id, url, imageWidth, imageHeight)