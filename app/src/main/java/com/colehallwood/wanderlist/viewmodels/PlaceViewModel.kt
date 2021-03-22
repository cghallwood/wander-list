package com.colehallwood.wanderlist.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.colehallwood.wanderlist.databases.PlaceDatabase
import com.colehallwood.wanderlist.repositories.PlaceRepository
import com.colehallwood.wanderlist.models.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceViewModel(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<List<Place>>
    private val repository: PlaceRepository

    init {
        val placeDao = PlaceDatabase.getDatabase(application).placeDao()
        repository = PlaceRepository(placeDao)
        readAllData = repository.readAllData
    }

    /**
     * Add [place] to the [repository]
     */
    fun addPlace(place: Place) {
        // Run from background thread
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPlace(place)
        }
    }

    /**
     * Delete [place] from [repository]
     */
    fun deletePlace(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlace(place)
        }
    }

    /**
     * Update [place] in the [repository]
     */
    fun updatePlace(place: Place) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePlace(place)
        }
    }
}