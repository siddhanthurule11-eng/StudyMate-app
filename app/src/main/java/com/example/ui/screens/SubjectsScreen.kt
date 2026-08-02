package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entities.SubjectEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.viewmodel.StudyMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    viewModel: StudyMateViewModel,
    onBack: () -> Unit
) {
    val subjectsList by viewModel.subjectsList.collectAsStateWithLifecycle()
    val homeworkList by viewModel.homeworkList.collectAsStateWithLifecycle()
    val notesList by viewModel.notesList.collectAsStateWithLifecycle()
    val timetableList by viewModel.allTimetableClasses.collectAsStateWithLifecycle()
    val examsList by viewModel.examsList.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingSubject by remember { mutableStateOf<SubjectEntity?>(null) }
    var selectedDetailSubject by remember { mutableStateOf<SubjectEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subjects & Courses", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BluePrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingSubject = null
                    showAddDialog = true
                },
                containerColor = BluePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Subject")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            Text(
                text = "My Enrolled Subjects (${subjectsList.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (subjectsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No subjects added yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subjectsList, key = { it.id }) { subject ->
                        val subjectHwCount = homeworkList.count { it.subject.equals(subject.name, ignoreCase = true) }
                        val subjectNotesCount = notesList.count { it.subject.equals(subject.name, ignoreCase = true) }

                        SubjectGridCard(
                            subject = subject,
                            hwCount = subjectHwCount,
                            notesCount = subjectNotesCount,
                            onClick = { selectedDetailSubject = subject },
                            onEdit = {
                                editingSubject = subject
                                showAddDialog = true
                            },
                            onDelete = { viewModel.deleteSubject(subject) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        SubjectAddEditDialog(
            initialSubject = editingSubject,
            onDismiss = { showAddDialog = false },
            onSave = { subject ->
                viewModel.addOrUpdateSubject(subject)
                showAddDialog = false
            }
        )
    }

    selectedDetailSubject?.let { subject ->
        val hwForSubject = homeworkList.filter { it.subject.equals(subject.name, ignoreCase = true) }
        val notesForSubject = notesList.filter { it.subject.equals(subject.name, ignoreCase = true) }
        val examsForSubject = examsList.filter { it.subject.equals(subject.name, ignoreCase = true) }

        AlertDialog(
            onDismissRequest = { selectedDetailSubject = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(subject.colorHex.ifEmpty { "#2563EB" })))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(subject.name, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Teacher: ${subject.teacher.ifEmpty { "Not specified" }}", fontSize = 13.sp)
                    Text("Classroom: ${subject.room.ifEmpty { "Not specified" }}", fontSize = 13.sp)

                    Divider()

                    Text("Homework (${hwForSubject.size}):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (hwForSubject.isEmpty()) {
                        Text("• No active homework", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        hwForSubject.forEach { hw ->
                            Text("• ${hw.title} (${hw.dueDate})", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Notes (${notesForSubject.size}):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (notesForSubject.isEmpty()) {
                        Text("• No saved notes", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        notesForSubject.forEach { note ->
                            Text("• ${note.title}", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Upcoming Exams (${examsForSubject.size}):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (examsForSubject.isEmpty()) {
                        Text("• No exams scheduled", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    } else {
                        examsForSubject.forEach { exam ->
                            Text("• ${exam.title} (${exam.examDateString})", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedDetailSubject = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SubjectGridCard(
    subject: SubjectEntity,
    hwCount: Int,
    notesCount: Int,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val cardColor = remember(subject.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(subject.colorHex))
        } catch (e: Exception) {
            BluePrimary
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(cardColor)
                )

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = BluePrimary, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subject.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Text(
                text = if (subject.teacher.isNotBlank()) subject.teacher else "No Teacher Set",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = BluePrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$hwCount HW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BluePrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "$notesCount Notes",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectAddEditDialog(
    initialSubject: SubjectEntity?,
    onDismiss: () -> Unit,
    onSave: (SubjectEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialSubject?.name ?: "") }
    var teacher by remember { mutableStateOf(initialSubject?.teacher ?: "") }
    var room by remember { mutableStateOf(initialSubject?.room ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialSubject == null) "Add Subject" else "Edit Subject", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Teacher / Professor") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Classroom / Room No.") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            SubjectEntity(
                                id = initialSubject?.id ?: 0,
                                name = name,
                                teacher = teacher,
                                room = room,
                                colorHex = initialSubject?.colorHex ?: "#2563EB"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Subject")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
