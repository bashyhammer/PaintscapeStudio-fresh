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

    @TypeConverter
    fun fromLongList(value: List<Long>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toLongList(value: String?): List<Long>? {
        if (value == null) return null
        val listType = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(value, listType)
    }
}