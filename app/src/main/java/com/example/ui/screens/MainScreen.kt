package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entities.StudentProfileEntity
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.PurpleSecondary
import com.example.ui.viewmodel.StudyMateViewModel

data class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun MainScreen(viewModel: StudyMateViewModel) {
    var currentRoute by remember { mutableStateOf("home") }
    val studentProfile by viewModel.studentProfile.collectAsStateWithLifecycle()

    val navItems = listOf(
        NavItem("home", "Home", Icons.Default.Home),
        NavItem("tasks", "Tasks", Icons.Default.Checklist),
        NavItem("notes", "Notes", Icons.Default.Description),
        NavItem("profile", "Profile", Icons.Default.Person)
    )

    val needsOnboarding = studentProfile == null || (studentProfile?.name.isNullOrBlank())

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf("home", "tasks", "notes", "profile")) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 12.dp
                ) {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.route
                        val iconScale by animateFloatAsState(
                            targetValue = if (selected) 1.2f else 1.0f,
                            animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
                            label = "NavIconScale"
                        )

                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentRoute = item.route },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    modifier = Modifier.scale(iconScale)
                                )
                            },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BluePrimary,
                                selectedTextColor = BluePrimary,
                                indicatorColor = BluePrimary.copy(alpha = 0.18f)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            AnimatedContent(
                targetState = currentRoute,
                transitionSpec = {
                    (fadeIn() + scaleIn(initialScale = 0.96f) + slideInHorizontally { width -> width / 12 })
                        .togetherWith(fadeOut() + scaleOut(targetScale = 0.96f))
                },
                label = "ScreenTransition"
            ) { targetRoute ->
                when (targetRoute) {
                    "home" -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToSection = { route -> currentRoute = route }
                    )
                    "tasks", "todo" -> TodoScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "timetable" -> TimetableScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "homework" -> HomeworkScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "notes" -> NotesScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "exams" -> ExamPlannerScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "calendar" -> CalendarScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "timer" -> StudyTimerScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "subjects" -> SubjectsScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "goals" -> GoalsScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "search" -> GlobalSearchScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" },
                        onNavigateToSection = { route -> currentRoute = route }
                    )
                    "profile" -> ProfileScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "settings" -> SettingsScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                    "gpa" -> GpaCalculatorScreen(
                        viewModel = viewModel,
                        onBack = { currentRoute = "home" }
                    )
                }
            }
        }
    }

    if (needsOnboarding) {
        OnboardingProfileDialog(
            currentProfile = studentProfile ?: StudentProfileEntity(),
            onSave = { updatedProfile ->
                viewModel.saveProfile(updatedProfile)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingProfileDialog(
    currentProfile: StudentProfileEntity,
    onSave: (StudentProfileEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var studentClass by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Student") }
    var school by remember { mutableStateOf("") }

    val roleOptions = listOf("Student", "High School Scholar", "College Student", "Undergraduate", "Learner")

    BasicAlertDialog(
        onDismissRequest = { /* Modal cannot be dismissed without completing setup */ }
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    color = BluePrimary.copy(alpha = 0.12f),
                    shape = CircleShape,
                    modifier = Modifier.size(60.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = "Welcome",
                            tint = BluePrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Text(
                    text = "Welcome to StudyMate! 👋",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Please enter your Name, Class, and Role to personalize your clean workspace.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Your Full Name") },
                    placeholder = { Text("e.g. Siddhant Hurule") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = studentClass,
                    onValueChange = { studentClass = it },
                    label = { Text("Class / Grade") },
                    placeholder = { Text("e.g. Grade 10 / Class 12 CS") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Role") },
                    placeholder = { Text("e.g. Student, College Scholar...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = school,
                    onValueChange = { school = it },
                    label = { Text("School / Institution (Optional)") },
                    placeholder = { Text("e.g. National Academy") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && studentClass.isNotBlank()) {
                            onSave(
                                currentProfile.copy(
                                    name = name.trim(),
                                    studentClass = studentClass.trim(),
                                    role = if (role.isNotBlank()) role.trim() else "Student",
                                    school = school.trim(),
                                    streakCount = 0,
                                    maxStreak = 0
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = name.isNotBlank() && studentClass.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Initialize Workspace (All Zero) →",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

