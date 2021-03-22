package com.colehallwood.wanderlist.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "place_table")
data class Place (
    @PrimaryKey(autoGenerate = true)
    val placeId: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
): Parcelable