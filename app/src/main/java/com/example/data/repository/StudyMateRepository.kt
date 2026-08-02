package com.example.data.repository

import com.example.data.dao.StudyMateDao
import com.example.data.entities.*
import kotlinx.coroutines.flow.Flow

class StudyMateRepository(private val dao: StudyMateDao) {

    // Timetable
    val allTimetableClasses: Flow<List<TimetableEntity>> = dao.getAllTimetableClasses()
    fun getTimetableForDay(day: String): Flow<List<TimetableEntity>> = dao.getTimetableForDay(day)
    suspend fun insertClass(classEntity: TimetableEntity) = dao.insertClass(classEntity)
    suspend fun updateClass(classEntity: TimetableEntity) = dao.updateClass(classEntity)
    suspend fun deleteClass(classEntity: TimetableEntity) = dao.deleteClass(classEntity)

    // Homework
    val allHomework: Flow<List<HomeworkEntity>> = dao.getAllHomework()
    suspend fun insertHomework(homework: HomeworkEntity) = dao.insertHomework(homework)
    suspend fun updateHomework(homework: HomeworkEntity) = dao.updateHomework(homework)
    suspend fun deleteHomework(homework: HomeworkEntity) = dao.deleteHomework(homework)

    // Notes
    val allNotes: Flow<List<NoteEntity>> = dao.getAllNotes()
    fun searchNotes(query: String): Flow<List<NoteEntity>> = dao.searchNotes(query)
    suspend fun insertNote(note: NoteEntity) = dao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = dao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = dao.deleteNote(note)

    // Todos
    val allTodos: Flow<List<TodoEntity>> = dao.getAllTodos()
    suspend fun insertTodo(todo: TodoEntity) = dao.insertTodo(todo)
    suspend fun updateTodo(todo: TodoEntity) = dao.updateTodo(todo)
    suspend fun deleteTodo(todo: TodoEntity) = dao.deleteTodo(todo)

    // Exams
    val allExams: Flow<List<ExamEntity>> = dao.getAllExams()
    suspend fun insertExam(exam: ExamEntity) = dao.insertExam(exam)
    suspend fun updateExam(exam: ExamEntity) = dao.updateExam(exam)
    suspend fun deleteExam(exam: ExamEntity) = dao.deleteExam(exam)

    // Student Profile
    val studentProfile: Flow<StudentProfileEntity?> = dao.getStudentProfile()
    suspend fun saveStudentProfile(profile: StudentProfileEntity) = dao.saveStudentProfile(profile)

    // Subjects
    val allSubjects: Flow<List<SubjectEntity>> = dao.getAllSubjects()
    suspend fun insertSubject(subject: SubjectEntity) = dao.insertSubject(subject)
    suspend fun updateSubject(subject: SubjectEntity) = dao.updateSubject(subject)
    suspend fun deleteSubject(subject: SubjectEntity) = dao.deleteSubject(subject)

    // Goals
    val allGoals: Flow<List<GoalEntity>> = dao.getAllGoals()
    suspend fun insertGoal(goal: GoalEntity) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = dao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = dao.deleteGoal(goal)
}
