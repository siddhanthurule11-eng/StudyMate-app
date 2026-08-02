package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String = "Daily", // Daily, Weekly, Monthly
    val targetValue: Int = 100,
    val currentValue: Int = 0,
    val isCompleted: Boolean = false,
    val dueDate: String = ""
)
