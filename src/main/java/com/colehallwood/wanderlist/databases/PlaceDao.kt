package com.colehallwood.wanderlist.databases

import androidx.lifecycle.LiveData
import androidx.room.*
import com.colehallwood.wanderlist.models.Place

@Dao
interface PlaceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlace(place: Place)

    @Update
    suspend fun updatePlace(place: Place)

    @Delete
    suspend fun deletePlace(place: Place)

    @Query("SELECT * FROM place_table ORDER BY placeId ASC")
    fun readAllData(): LiveData<List<Place>>
}