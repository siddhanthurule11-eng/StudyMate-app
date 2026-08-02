package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subject: String,
    val title: String,
    val examDateMillis: Long,
    val examDateString: String,
    val examTimeString: String = "10:00 AM",
    val room: String = "",
    val notes: String = ""
)
