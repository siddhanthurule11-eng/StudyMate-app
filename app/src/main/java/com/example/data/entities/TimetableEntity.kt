package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val teacher: String,
    val room: String,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val colorHex: String = "#2563EB"
)
