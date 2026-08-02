package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profile")
data class StudentProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val studentClass: String = "",
    val role: String = "Student",
    val school: String = "",
    val email: String = "",
    val phone: String = "",
    val bio: String = "",
    val streakCount: Int = 0,
    val lastStreakDate: String = "",
    val maxStreak: Int = 0,
    val profileImageUri: String? = null
)

