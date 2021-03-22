package com.colehallwood.wanderlist.repositories

import androidx.lifecycle.LiveData
import com.colehallwood.wanderlist.databases.PlaceDao
import com.colehallwood.wanderlist.models.Place

class PlaceRepository(private val placeDao: PlaceDao) {

    val readAllData: LiveData<List<Place>> = placeDao.readAllData()

    /**
     * Add [place] to the [placeDao]
     */
    suspend fun addPlace(place: Place) {
        placeDao.addPlace(place)
    }

    /**
     * Delete [place] from [placeDao]
     */
    suspend fun deletePlace(place: Place) {
        placeDao.deletePlace(place)
    }

    /**
     * Update [place] in the [placeDao]
     */
    suspend fun updatePlace(place: Place) {
        placeDao.updatePlace(place)
    }
}