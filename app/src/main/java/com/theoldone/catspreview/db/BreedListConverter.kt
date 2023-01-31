package com.theoldone.catspreview.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.theoldone.catspreview.db.models.BreedDBModel

class BreedListConverter {
	var gson = Gson()

	@TypeConverter
	fun fromString(data: String?): List<BreedDBModel> {
		if (data == null)
			return emptyList()

		val listType = object : TypeToken<List<BreedDBModel?>?>() {}.type
		return gson.fromJson(data, listType)
	}

	@TypeConverter
	fun fromList(someObjects: List<BreedDBModel?>?) = gson.toJson(someObjects)
}