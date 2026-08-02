package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_tasks")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val task: String,
    val category: String = "Study",
    val priority: String = "Medium",
    val dueDate: String = "",
    val isCompleted: Boolean = false
)
