package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.StudyMateDao
import com.example.data.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TimetableEntity::class,
        HomeworkEntity::class,
        NoteEntity::class,
        TodoEntity::class,
        ExamEntity::class,
        StudentProfileEntity::class,
        SubjectEntity::class,
        GoalEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studyMateDao(): StudyMateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "studymate_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.studyMateDao())
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: StudyMateDao) {
            // Clean database startup: no mock homework, tasks, or subjects added by default.
            dao.saveStudentProfile(
                StudentProfileEntity(
                    id = 1,
                    name = "",
                    studentClass = "",
                    role = "Student",
                    school = "",
                    email = "",
                    bio = "",
                    streakCount = 0,
                    lastStreakDate = "",
                    maxStreak = 0
                )
            )
        }
    }
}
