package com.paintscape.studio.util

import android.content.Context
import com.google.gson.Gson
import com.paintscape.studio.data.model.SegmentationData
import java.io.InputStreamReader
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext

class SegmentationEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    // 1. This scans your assets/animals folder for files
    fun getAvailableAnimals(): List<String> {
        return try {
            context.assets.list("animals")?.map { it.removeSuffix(".json") } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 2. This opens the art_001.json (or any animal) and reads it
    fun loadAnimalData(animalName: String): SegmentationData? {
        return try {
            val inputStream = context.assets.open("animals/$animalName.json")
            val reader = InputStreamReader(inputStream)
            gson.fromJson(reader, SegmentationData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}