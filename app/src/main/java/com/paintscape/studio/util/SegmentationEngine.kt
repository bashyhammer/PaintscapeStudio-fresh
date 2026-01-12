package com.paintscape.studio.util

import android.content.Context
import com.google.gson.Gson
import com.paintscape.studio.data.model.SegmentationData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject

class SegmentationEngine @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()

    // Scans your assets/animals folder for files
    fun getAvailableAnimals(): List<String> {
        return try {
            context.assets.list("animals")?.map { it.removeSuffix(".json") } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Opens the JSON file and parses it on a background thread
    suspend fun loadAnimalData(animalName: String): SegmentationData? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("animals/$animalName.json")
                val reader = InputStreamReader(inputStream)
                gson.fromJson(reader, SegmentationData::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}