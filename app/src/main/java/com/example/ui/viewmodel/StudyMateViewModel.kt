package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entities.*
import com.example.data.repository.StudyMateRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class StudyMateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudyMateRepository

    init {
        val dao = AppDatabase.getDatabase(application).studyMateDao()
        repository = StudyMateRepository(dao)
        checkAndResetStreakOnStartup()
    }

    /**
     * Startup logic that verifies if the user failed to open or perform activity in the app for > 24 hours.
     * If a day was missed (lastStreakDate is older than yesterday), resets streakCount to 0 or 1 upon today's check-in.
     */
    fun checkAndResetStreakOnStartup() {
        viewModelScope.launch {
            val currentProfile = repository.studentProfile.firstOrNull() ?: StudentProfileEntity()
            val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val todayStr = dateFormat.format(java.util.Date())
            val lastDateStr = currentProfile.lastStreakDate

            if (lastDateStr == todayStr) {
                // User already opened/checked in today, streak remains accurate
                return@launch
            }

            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = dateFormat.format(cal.time)

            if (lastDateStr.isNotEmpty() && lastDateStr != yesterdayStr) {
                // Inactivity exceeded 24 hours (missed yesterday) - streak is broken and reset to 1 for today's new session
                val updatedProfile = currentProfile.copy(
                    streakCount = 1,
                    lastStreakDate = todayStr,
                    maxStreak = maxOf(currentProfile.maxStreak, 1)
                )
                repository.saveStudentProfile(updatedProfile)
            } else if (lastDateStr == yesterdayStr) {
                // User opened on consecutive day! Increment streak count
                val newStreak = currentProfile.streakCount + 1
                val updatedProfile = currentProfile.copy(
                    streakCount = newStreak,
                    lastStreakDate = todayStr,
                    maxStreak = maxOf(currentProfile.maxStreak, newStreak)
                )
                repository.saveStudentProfile(updatedProfile)
            } else if (lastDateStr.isEmpty()) {
                // Initial check-in
                val updatedProfile = currentProfile.copy(
                    streakCount = if (currentProfile.streakCount > 0) currentProfile.streakCount else 1,
                    lastStreakDate = todayStr,
                    maxStreak = maxOf(currentProfile.maxStreak, 1)
                )
                repository.saveStudentProfile(updatedProfile)
            }
        }
    }

    fun recordActivityForStreak() {
        checkAndResetStreakOnStartup()
    }

    // Timetable
    private val _selectedDay = MutableStateFlow("Monday")
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    val timetableForSelectedDay: StateFlow<List<TimetableEntity>> = _selectedDay
        .flatMapLatest { day -> repository.getTimetableForDay(day) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTimetableClasses: StateFlow<List<TimetableEntity>> = repository.allTimetableClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
    }

    fun addOrUpdateClass(classEntity: TimetableEntity) {
        viewModelScope.launch {
            if (classEntity.id == 0) {
                repository.insertClass(classEntity)
            } else {
                repository.updateClass(classEntity)
            }
        }
    }

    fun deleteClass(classEntity: TimetableEntity) {
        viewModelScope.launch {
            repository.deleteClass(classEntity)
        }
    }

    // Homework
    val homeworkList: StateFlow<List<HomeworkEntity>> = repository.allHomework
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addOrUpdateHomework(homework: HomeworkEntity) {
        viewModelScope.launch {
            if (homework.id == 0) {
                repository.insertHomework(homework)
            } else {
                repository.updateHomework(homework)
            }
        }
    }

    fun toggleHomeworkCompleted(homework: HomeworkEntity) {
        viewModelScope.launch {
            val newStatus = !homework.isCompleted
            repository.updateHomework(homework.copy(isCompleted = newStatus))
            if (newStatus) {
                recordActivityForStreak()
            }
        }
    }

    fun deleteHomework(homework: HomeworkEntity) {
        viewModelScope.launch {
            repository.deleteHomework(homework)
        }
    }

    // Notes
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val notesList: StateFlow<List<NoteEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allNotes
            else repository.searchNotes(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addOrUpdateNote(note: NoteEntity) {
        viewModelScope.launch {
            if (note.id == 0) {
                repository.insertNote(note)
            } else {
                repository.updateNote(note)
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    // To-Do List
    val todoList: StateFlow<List<TodoEntity>> = repository.allTodos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addOrUpdateTodo(todo: TodoEntity) {
        viewModelScope.launch {
            if (todo.id == 0) {
                repository.insertTodo(todo)
            } else {
                repository.updateTodo(todo)
            }
        }
    }

    fun toggleTodoCompleted(todo: TodoEntity) {
        viewModelScope.launch {
            val newStatus = !todo.isCompleted
            repository.updateTodo(todo.copy(isCompleted = newStatus))
            if (newStatus) {
                recordActivityForStreak()
            }
        }
    }

    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }

    // Exam Planner
    val examsList: StateFlow<List<ExamEntity>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addOrUpdateExam(exam: ExamEntity) {
        viewModelScope.launch {
            if (exam.id == 0) {
                repository.insertExam(exam)
            } else {
                repository.updateExam(exam)
            }
        }
    }

    fun deleteExam(exam: ExamEntity) {
        viewModelScope.launch {
            repository.deleteExam(exam)
        }
    }

    // Student Profile
    val studentProfile: StateFlow<StudentProfileEntity?> = repository.studentProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveProfile(profile: StudentProfileEntity) {
        viewModelScope.launch {
            repository.saveStudentProfile(profile)
        }
    }

    // Subjects
    val subjectsList: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addOrUpdateSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            if (subject.id == 0) {
                repository.insertSubject(subject)
            } else {
                repository.updateSubject(subject)
            }
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    // Goals
    val goalsList: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addOrUpdateGoal(goal: GoalEntity) {
        viewModelScope.launch {
            if (goal.id == 0) {
                repository.insertGoal(goal)
            } else {
                repository.updateGoal(goal)
            }
        }
    }

    fun toggleGoalCompleted(goal: GoalEntity) {
        viewModelScope.launch {
            val newCompleted = !goal.isCompleted
            val updated = goal.copy(
                isCompleted = newCompleted,
                currentValue = if (newCompleted) goal.targetValue else 0
            )
            repository.updateGoal(updated)
            if (newCompleted) {
                recordActivityForStreak()
            }
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // Note actions
    fun togglePinNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned))
        }
    }

    fun toggleFavoriteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isFavorite = !note.isFavorite))
        }
    }

    fun duplicateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.insertNote(note.copy(id = 0, title = "${note.title} (Copy)"))
        }
    }

    fun duplicateHomework(homework: HomeworkEntity) {
        viewModelScope.launch {
            repository.insertHomework(homework.copy(id = 0, title = "${homework.title} (Copy)"))
        }
    }

    fun duplicateTodo(todo: TodoEntity) {
        viewModelScope.launch {
            repository.insertTodo(todo.copy(id = 0, task = "${todo.task} (Copy)"))
        }
    }

    // Theme Mode
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }
}
