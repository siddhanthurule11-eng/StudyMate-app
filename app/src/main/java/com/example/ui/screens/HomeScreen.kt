package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudyMateViewModel

@Composable
fun HomeScreen(
    viewModel: StudyMateViewModel,
    onNavigateToSection: (String) -> Unit
) {
    val profile by viewModel.studentProfile.collectAsStateWithLifecycle()
    val homeworkList by viewModel.homeworkList.collectAsStateWithLifecycle()
    val todoList by viewModel.todoList.collectAsStateWithLifecycle()
    val examsList by viewModel.examsList.collectAsStateWithLifecycle()
    val notesList by viewModel.notesList.collectAsStateWithLifecycle()
    val subjectsList by viewModel.subjectsList.collectAsStateWithLifecycle()
    val goalsList by viewModel.goalsList.collectAsStateWithLifecycle()

    val pendingHomeworkCount = homeworkList.count { !it.isCompleted }
    val pendingTodoCount = todoList.count { !it.isCompleted }
    val upcomingExamsCount = examsList.size
    val activeGoalsCount = goalsList.count { !it.isCompleted }

    val studentName = if (!profile?.name.isNullOrBlank()) profile!!.name else "Student Workspace"
    val studentRole = if (!profile?.role.isNullOrBlank()) profile!!.role else "Student"
    val studentClass = if (!profile?.studentClass.isNullOrBlank()) profile!!.studentClass else "Setup Profile"
    val streakCount = profile?.streakCount ?: 0

    val infiniteTransition = rememberInfiniteTransition(label = "FlamePulse")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FlameScale"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Banner Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                BluePrimary,
                                PurpleSecondary,
                                BlueDark
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Welcome back 👋",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = studentName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "🎓 $studentRole",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "📚 $studentClass",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Surface(
                                    color = AmberStreak.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Whatshot,
                                            contentDescription = null,
                                            tint = AmberStreak,
                                            modifier = Modifier
                                                .size(13.dp)
                                                .scale(flameScale)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "$streakCount Days",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = { onNavigateToSection("search") },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickStatBadge(
                            label = "Homework",
                            count = "$pendingHomeworkCount Pending",
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            containerColor = Color.White.copy(alpha = 0.18f),
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatBadge(
                            label = "To-Do",
                            count = "$pendingTodoCount Active",
                            icon = Icons.Default.CheckCircle,
                            containerColor = Color.White.copy(alpha = 0.18f),
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatBadge(
                            label = "Exams",
                            count = "$upcomingExamsCount Soon",
                            icon = Icons.Default.Event,
                            containerColor = Color.White.copy(alpha = 0.18f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Quote of the Day
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = BluePrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.padding(8.dp)) {
                            Icon(Icons.Default.FormatQuote, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "\"Consistency is what transforms average into excellence.\"",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Daily Student Motivation ✨",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Feature Grid Section Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                Text(
                    text = "Study Hub & Tools",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Everything you need to excel in your studies",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Feature Cards List
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DashboardFeatureCard(
                    title = "Timetable",
                    subtitle = "Weekly Schedule & Classrooms",
                    badge = "7 Days",
                    icon = Icons.Default.Schedule,
                    cardBgColor = MaterialTheme.colorScheme.surface,
                    iconBgColor = BluePrimary,
                    onClick = { onNavigateToSection("timetable") }
                )

                DashboardFeatureCard(
                    title = "Homework Tracker",
                    subtitle = "Assignments & Submission Deadlines",
                    badge = if (pendingHomeworkCount > 0) "$pendingHomeworkCount Pending" else "All Done",
                    badgeColor = if (pendingHomeworkCount > 0) PriorityHigh else GreenAccent,
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    cardBgColor = MaterialTheme.colorScheme.surface,
                    iconBgColor = PriorityHigh,
                    onClick = { onNavigateToSection("homework") }
                )

                DashboardFeatureCard(
                    title = "Study Notes",
                    subtitle = "Rich Summaries & Color Labels",
                    badge = "${notesList.size} Saved",
                    icon = Icons.Default.Description,
                    cardBgColor = MaterialTheme.colorScheme.surface,
                    iconBgColor = GreenAccent,
                    onClick = { onNavigateToSection("notes") }
                )

                DashboardFeatureCard(
                    title = "To-Do List",
                    subtitle = "Daily Study Tasks & Checklists",
                    badge = if (pendingTodoCount > 0) "$pendingTodoCount Active" else "Completed",
                    icon = Icons.Default.Checklist,
                    cardBgColor = MaterialTheme.colorScheme.surface,
                    iconBgColor = PriorityMedium,
                    onClick = { onNavigateToSection("todo") }
                )

                DashboardFeatureCard(
                    title = "Exam Planner",
                    subtitle = "Schedules & Live Countdown Timers",
                    badge = "$upcomingExamsCount Scheduled",
                    icon = Icons.Default.School,
                    cardBgColor = MaterialTheme.colorScheme.surface,
                    iconBgColor = PurpleSecondary,
                    onClick = { onNavigateToSection("exams") }
                )

                DashboardFeatureCard(
                    title = "Study Calendar",
                    subtitle = "Monthly Agenda, Exams & Reminders",
                    badge = "Monthly",
                    icon = Icons.Default.CalendarMonth,
                    cardBgColor = MaterialTheme.colorScheme.surface,
                    iconBgColor = CyanAccent,
                    onClick = { onNavigateToSection("calendar") }
                )

                DashboardFeatureCard(
                    title = "Pomodoro Study Timer",
                    subtitle = "25/5 Focus Mode & Statistics",
                    badge = "Focus Mode",
                    icon = Icons.Default.Timer,
                    cardBgColor = MaterialTheme.colorScheme.surface,
                    iconBgColor = GreenAccent,
                    onClick = { onNavigateToSection("timer") }
                )

                DashboardFeatureCard(
                    title = "Subjects & Courses",
                    subtitle = "Teachers, Rooms & Subject Breakdown",
                    badge = "${subjectsList.size} Enrolled",
                    icon = Icons.Default.Book,
                    cardBgColor = MaterialTheme.colorScheme.surface,
                    iconBgColor = AmberStreak,
                    onClick = { onNavigateToSection("subjects") }
                )

                DashboardFeatureCard(
                    title = "Goals & Achievements",
                    subtitle = "Daily / Weekly Targets & Progress",
                    badge = "$activeGoalsCount Active",
                    icon = Icons.Default.EmojiEvents,
                    cardBgColor = MaterialTheme.colorScheme.surface,
                    iconBgColor = PinkAccent,
                    onClick = { onNavigateToSection("goals") }
                )

                DashboardFeatureCard(
                    title = "GPA & Marks Calculator",
                    subtitle = "Calculate Grade %, CGPA & Credits",
                    badge = "Grade Tool",
                    icon = Icons.Default.Calculate,
                    cardBgColor = MaterialTheme.colorScheme.surface,
                    iconBgColor = Color(0xFF6366F1),
                    onClick = { onNavigateToSection("gpa") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SmallFeatureCard(
                        title = "Profile",
                        subtitle = "Student ID",
                        icon = Icons.Default.Person,
                        cardBgColor = MaterialTheme.colorScheme.surface,
                        iconColor = BluePrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("profile") }
                    )

                    SmallFeatureCard(
                        title = "Settings",
                        subtitle = "Theme & Data",
                        icon = Icons.Default.Settings,
                        cardBgColor = MaterialTheme.colorScheme.surface,
                        iconColor = Color(0xFF94A3B8),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("settings") }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatBadge(
    label: String,
    count: String,
    icon: ImageVector,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
fun DashboardFeatureCard(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color = BluePrimary,
    icon: ImageVector,
    cardBgColor: Color,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "CardPressScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = iconBgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = badgeColor.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SmallFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    cardBgColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

