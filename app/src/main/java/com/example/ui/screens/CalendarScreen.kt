package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entities.TodoEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.PinkAccent
import com.example.ui.theme.PriorityHigh
import com.example.ui.theme.PurpleSecondary
import com.example.ui.viewmodel.StudyMateViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: StudyMateViewModel,
    onBack: () -> Unit
) {
    val homeworkList by viewModel.homeworkList.collectAsStateWithLifecycle()
    val examsList by viewModel.examsList.collectAsStateWithLifecycle()
    val timetableList by viewModel.allTimetableClasses.collectAsStateWithLifecycle()
    val todoList by viewModel.todoList.collectAsStateWithLifecycle()

    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDayOfMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    val monthYearFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }
    val dayOfWeekFormat = remember { SimpleDateFormat("EEEE", Locale.getDefault()) }

    // Calculate calendar grid for selected month
    val currentMonthCalendar = remember(calendar.timeInMillis) {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        Pair(firstDayOfWeek, daysInMonth)
    }

    val (firstDayOffset, totalDaysInMonth) = currentMonthCalendar

    val selectedDateCalendar = remember(calendar.timeInMillis, selectedDayOfMonth) {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth)
        cal
    }

    val selectedDayName = remember(selectedDateCalendar.timeInMillis) {
        dayOfWeekFormat.format(selectedDateCalendar.time)
    }

    val selectedDateStr = remember(selectedDateCalendar.timeInMillis) {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(selectedDateCalendar.time)
    }

    val isTodaySelected = remember(selectedDateCalendar.timeInMillis) {
        val today = Calendar.getInstance()
        selectedDayOfMonth == today.get(Calendar.DAY_OF_MONTH) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    }

    // Filter events for selected date
    val dayClasses = remember(timetableList, selectedDayName) {
        timetableList.filter { it.dayOfWeek.equals(selectedDayName, ignoreCase = true) }
    }

    val dayExams = remember(examsList, selectedDateStr) {
        examsList.filter { it.examDateString.contains(selectedDateStr.take(6), ignoreCase = true) }
    }

    val dayHomework = remember(homeworkList, selectedDateStr, isTodaySelected) {
        homeworkList.filter {
            it.dueDate.contains(selectedDateStr.take(6), ignoreCase = true) ||
                    (it.dueDate.equals("Today", ignoreCase = true) && isTodaySelected)
        }
    }

    val dayReminders = remember(todoList, selectedDateStr, selectedDayOfMonth, isTodaySelected) {
        todoList.filter { todo ->
            todo.dueDate.contains(selectedDateStr.take(6), ignoreCase = true) ||
                    todo.dueDate.contains(selectedDateStr, ignoreCase = true) ||
                    (selectedDayOfMonth == 22 && (todo.task.contains("22") || todo.dueDate.contains("22"))) ||
                    (todo.dueDate.equals("Today", ignoreCase = true) && isTodaySelected)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Calendar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddReminderDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Reminder", tint = Color.White)
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
            ExtendedFloatingActionButton(
                onClick = { showAddReminderDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Event for Day $selectedDayOfMonth") },
                containerColor = BluePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(18.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calendar Header & Grid Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Month Selector Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                val cal = calendar.clone() as Calendar
                                cal.add(Calendar.MONTH, -1)
                                calendar = cal
                                selectedDayOfMonth = 1
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                            }

                            Text(
                                text = monthYearFormat.format(calendar.time),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(onClick = {
                                val cal = calendar.clone() as Calendar
                                cal.add(Calendar.MONTH, 1)
                                calendar = cal
                                selectedDayOfMonth = 1
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Days of Week Header Row
                        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            daysOfWeek.forEach { day ->
                                Text(
                                    text = day,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Days Grid
                        val totalCells = firstDayOffset + totalDaysInMonth
                        val rows = (totalCells + 6) / 7

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            for (r in 0 until rows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    for (c in 0 until 7) {
                                        val dayIndex = r * 7 + c - firstDayOffset + 1
                                        if (dayIndex in 1..totalDaysInMonth) {
                                            val isSelected = (dayIndex == selectedDayOfMonth)
                                            val isToday = dayIndex == Calendar.getInstance().get(Calendar.DAY_OF_MONTH) &&
                                                    calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) &&
                                                    calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)

                                            // Check if day has special event/reminder or is 22
                                            val hasDayEvent = dayIndex == 22 || todoList.any { it.dueDate.contains("$dayIndex") }

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .aspectRatio(1f)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when {
                                                            isSelected -> BluePrimary
                                                            isToday -> BluePrimary.copy(alpha = 0.15f)
                                                            hasDayEvent -> PurpleSecondary.copy(alpha = 0.18f)
                                                            else -> Color.Transparent
                                                        }
                                                    )
                                                    .clickable {
                                                        selectedDayOfMonth = dayIndex
                                                        showAddReminderDialog = true
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center
                                                ) {
                                                    Text(
                                                        text = dayIndex.toString(),
                                                        fontSize = 14.sp,
                                                        fontWeight = if (isSelected || isToday || hasDayEvent) FontWeight.Bold else FontWeight.Normal,
                                                        color = when {
                                                            isSelected -> Color.White
                                                            isToday -> BluePrimary
                                                            hasDayEvent -> PurpleSecondary
                                                            else -> MaterialTheme.colorScheme.onSurface
                                                        }
                                                    )
                                                    if (isSelected || hasDayEvent) {
                                                        Row(
                                                            horizontalArrangement = Arrangement.Center,
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(4.dp)
                                                                    .clip(CircleShape)
                                                                    .background(if (isSelected) Color.White else PinkAccent)
                                                            )
                                                            Spacer(modifier = Modifier.width(2.dp))
                                                            Icon(
                                                                imageVector = Icons.Default.Add,
                                                                contentDescription = null,
                                                                tint = if (isSelected) Color.White else PinkAccent,
                                                                modifier = Modifier.size(8.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Schedule Title Card for Selected Date
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Schedule & Reminders for Day $selectedDayOfMonth",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$selectedDayName, $selectedDateStr",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        onClick = { showAddReminderDialog = true },
                        color = BluePrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = BluePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add (+)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                        }
                    }
                }
            }

            // Date 22 Special Reminder Banner
            if (selectedDayOfMonth == 22) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = PinkAccent.copy(alpha = 0.12f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = PinkAccent, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Reminder for Day 22 📌",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PinkAccent
                                )
                                Text(
                                    text = "Important study tasks, homework submission check & weekly progress review.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Reminders / To-Dos Section
            if (dayReminders.isNotEmpty()) {
                item {
                    Text("Reminders & Tasks", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PurpleSecondary)
                }
                items(dayReminders) { reminder ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = reminder.isCompleted,
                                onCheckedChange = { viewModel.toggleTodoCompleted(reminder) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reminder.task,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Category: ${reminder.category} • Priority: ${reminder.priority}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Classes Section
            if (dayClasses.isNotEmpty()) {
                item {
                    Text("Classes & Timetable", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                }
                items(dayClasses) { cls ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BluePrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cls.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("${cls.teacher} • ${cls.room}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
                                Text("${cls.startTime} - ${cls.endTime}", fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // Exams Section
            if (dayExams.isNotEmpty()) {
                item {
                    Text("Exams Scheduled", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PriorityHigh)
                }
                items(dayExams) { exam ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = PriorityHigh.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Event, contentDescription = null, tint = PriorityHigh, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(exam.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text("${exam.subject} • ${exam.room}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Surface(color = PriorityHigh.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                                Text(exam.examTimeString, fontSize = 11.sp, color = PriorityHigh, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Homework Section
            if (dayHomework.isNotEmpty()) {
                item {
                    Text("Homework & Assignments Due", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
                }
                items(dayHomework) { hw ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = hw.isCompleted,
                                onCheckedChange = { viewModel.toggleHomeworkCompleted(hw) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(hw.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(hw.subject, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            if (dayClasses.isEmpty() && dayExams.isEmpty() && dayHomework.isEmpty() && dayReminders.isEmpty() && selectedDayOfMonth != 22) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    ) {
                        Box(modifier = Modifier.padding(32.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No events or reminders on $selectedDateStr", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { showAddReminderDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Reminder (+)")
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (showAddReminderDialog) {
        AddCalendarReminderDialog(
            dateString = "$selectedDateStr (Day $selectedDayOfMonth)",
            onDismiss = { showAddReminderDialog = false },
            onAdd = { taskTitle, category, priority ->
                viewModel.addOrUpdateTodo(
                    TodoEntity(
                        task = taskTitle,
                        category = category,
                        priority = priority,
                        dueDate = "Day $selectedDayOfMonth ($selectedDateStr)",
                        isCompleted = false
                    )
                )
                showAddReminderDialog = false
            }
        )
    }
}

@Composable
fun AddCalendarReminderDialog(
    dateString: String,
    onDismiss: () -> Unit,
    onAdd: (title: String, category: String, priority: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Reminder") }
    var priority by remember { mutableStateOf("High") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = BluePrimary.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, contentDescription = "Add", tint = BluePrimary)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Add Event / Task (+)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(dateString, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("What to do on this day") },
                    placeholder = { Text("e.g. Study Physics, Submit Assignment...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") },
                    placeholder = { Text("e.g. Study, Exam, Project, Personal") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Priority Level", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("High", "Medium", "Low").forEach { level ->
                        FilterChip(
                            selected = priority == level,
                            onClick = { priority = level },
                            label = { Text(level) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onAdd(title, category, priority)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Task (+)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

