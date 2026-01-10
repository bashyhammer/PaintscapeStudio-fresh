package com.paintscape.studio.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromIntSet(value: Set<Int>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toIntSet(value: String?): Set<Int>? {
        if (value == null) return null
        val setType = object : TypeToken<Set<Int>>() {}.type
        return gson.fromJson(value, setType)
    }
}