package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.PriorityHigh
import com.example.ui.viewmodel.StudyMateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSearchScreen(
    viewModel: StudyMateViewModel,
    onBack: () -> Unit,
    onNavigateToSection: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val notesList by viewModel.notesList.collectAsStateWithLifecycle()
    val homeworkList by viewModel.homeworkList.collectAsStateWithLifecycle()
    val todoList by viewModel.todoList.collectAsStateWithLifecycle()
    val examsList by viewModel.examsList.collectAsStateWithLifecycle()
    val subjectsList by viewModel.subjectsList.collectAsStateWithLifecycle()

    val filteredNotes = remember(notesList, query) {
        if (query.isBlank()) emptyList()
        else notesList.filter { it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true) || it.subject.contains(query, ignoreCase = true) }
    }

    val filteredHomework = remember(homeworkList, query) {
        if (query.isBlank()) emptyList()
        else homeworkList.filter { it.title.contains(query, ignoreCase = true) || it.subject.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    }

    val filteredTodos = remember(todoList, query) {
        if (query.isBlank()) emptyList()
        else todoList.filter { it.task.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
    }

    val filteredExams = remember(examsList, query) {
        if (query.isBlank()) emptyList()
        else examsList.filter { it.title.contains(query, ignoreCase = true) || it.subject.contains(query, ignoreCase = true) }
    }

    val filteredSubjects = remember(subjectsList, query) {
        if (query.isBlank()) emptyList()
        else subjectsList.filter { it.name.contains(query, ignoreCase = true) || it.teacher.contains(query, ignoreCase = true) }
    }

    val totalMatches = filteredNotes.size + filteredHomework.size + filteredTodos.size + filteredExams.size + filteredSubjects.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global Search", fontWeight = FontWeight.Bold) },
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search across all notes, homework, tasks, exams...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BluePrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            if (query.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Type anything to search across StudyMate", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else if (totalMatches == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No results found for '$query'", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (filteredNotes.isNotEmpty()) {
                        item {
                            Text("Notes (${filteredNotes.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                        }
                        items(filteredNotes) { note ->
                            SearchResultCard(
                                title = note.title,
                                subtitle = note.content,
                                badge = note.subject,
                                icon = Icons.Default.Description,
                                onClick = { onNavigateToSection("notes") }
                            )
                        }
                    }

                    if (filteredHomework.isNotEmpty()) {
                        item {
                            Text("Homework (${filteredHomework.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PriorityHigh)
                        }
                        items(filteredHomework) { hw ->
                            SearchResultCard(
                                title = hw.title,
                                subtitle = "Due: ${hw.dueDate}",
                                badge = hw.subject,
                                icon = Icons.AutoMirrored.Filled.MenuBook,
                                onClick = { onNavigateToSection("homework") }
                            )
                        }
                    }

                    if (filteredTodos.isNotEmpty()) {
                        item {
                            Text("To-Do Tasks (${filteredTodos.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
                        }
                        items(filteredTodos) { todo ->
                            SearchResultCard(
                                title = todo.task,
                                subtitle = "Category: ${todo.category}",
                                badge = todo.priority,
                                icon = Icons.Default.Checklist,
                                onClick = { onNavigateToSection("todo") }
                            )
                        }
                    }

                    if (filteredExams.isNotEmpty()) {
                        item {
                            Text("Exams (${filteredExams.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA855F7))
                        }
                        items(filteredExams) { exam ->
                            SearchResultCard(
                                title = exam.title,
                                subtitle = "Date: ${exam.examDateString}",
                                badge = exam.subject,
                                icon = Icons.Default.School,
                                onClick = { onNavigateToSection("exams") }
                            )
                        }
                    }

                    if (filteredSubjects.isNotEmpty()) {
                        item {
                            Text("Subjects (${filteredSubjects.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        }
                        items(filteredSubjects) { subj ->
                            SearchResultCard(
                                title = subj.name,
                                subtitle = "Teacher: ${subj.teacher}",
                                badge = subj.room,
                                icon = Icons.Default.Book,
                                onClick = { onNavigateToSection("subjects") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = BluePrimary.copy(alpha = 0.12f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            if (badge.isNotBlank()) {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
                    Text(badge, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
