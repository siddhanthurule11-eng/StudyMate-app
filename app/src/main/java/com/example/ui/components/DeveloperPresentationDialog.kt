package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperPresentationDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BluePrimary)
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "PROJECT PRESENTATION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "StudyMate",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Student Study Planner Application",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.clip(CircleShape)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Code,
                                    contentDescription = null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Developer: Siddhant Hurule",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BluePrimary
                                )
                            }
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Introduction Script
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = BluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Presentation Speech", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Good morning/afternoon respected teacher and my dear friends.\n\n" +
                                        "My name is Siddhant Hurule, and today I am presenting my Android application called \"StudyMate\".\n\n" +
                                        "StudyMate is a student study planner application designed to help students organize their academic work.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Technologies Used
                    PresentationSection(
                        icon = Icons.Default.Build,
                        title = "Technologies Used",
                        iconColor = BluePrimary,
                        items = listOf(
                            "Android Studio Project & Gradle Build System",
                            "Kotlin Programming Language",
                            "Jetpack Compose & Material Design 3 for a modern UI",
                            "Room Database (SQLite) for offline data storage",
                            "MVVM (Model-View-ViewModel) Architecture",
                            "Repository Pattern for managing data",
                            "Android Jetpack Components & Coroutines / StateFlow",
                            "Navigation Component for smooth screen flow",
                            "Coil Image Loader for student profile photos"
                        )
                    )

                    // Features of the Application
                    PresentationSection(
                        icon = Icons.Default.Stars,
                        title = "Features of the Application",
                        iconColor = GreenAccent,
                        items = listOf(
                            "Animated Splash Screen & StudyMate Logo",
                            "Developer Credit: Made by Siddhant Hurule",
                            "Home Dashboard with Study Streak Counter",
                            "Timetable Management & Daily Schedule",
                            "Homework Manager with Status Badges",
                            "Notes Manager with Subject Filtering",
                            "To-Do List & Exam Planner",
                            "Student Profile with Custom Photo Upload",
                            "Settings Screen & Developer Presentation",
                            "100% Offline SQLite Storage (Full CRUD operations)"
                        )
                    )

                    // How the Application Works
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SettingsSuggest, contentDescription = null, tint = Color(0xFFF59E0B))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("How The Application Works", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "• The application stores all user data locally using the Room Database (SQLite).\n" +
                                        "• Whenever the application is closed and reopened, saved data is automatically loaded.\n" +
                                        "• Users can add, edit, update, and delete their study information freely.\n" +
                                        "• Main features work 100% offline without needing an active internet connection.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Project Structure
                    PresentationSection(
                        icon = Icons.Default.AccountTree,
                        title = "Project Structure",
                        iconColor = Color(0xFF9333EA),
                        items = listOf(
                            "Activities & Compose Screen Layouts",
                            "Kotlin Source Files & Clean Data Entities",
                            "Database Layer (Room & DAO Interfaces)",
                            "ViewModel Layer (StudyMateViewModel)",
                            "Repository Layer (StudyMateRepository)",
                            "Resources (Icons, Custom Colors, Strings)",
                            "Android Manifest & Gradle Configurations"
                        )
                    )

                    // Future Improvements
                    PresentationSection(
                        icon = Icons.Default.RocketLaunch,
                        title = "Future Improvements",
                        iconColor = Color(0xFFEC4899),
                        items = listOf(
                            "Cloud Backup & Google Sync",
                            "Student Login & Authentication System",
                            "Push Notifications for Exam Reminders",
                            "Pomodoro Study Timer Integration",
                            "Google Calendar Synchronization",
                            "Study Statistics & AI-powered Study Assistant"
                        )
                    )

                    // Conclusion
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BluePrimary.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, contentDescription = null, tint = BluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Conclusion", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Through this project, I learned how an Android application is planned, designed, developed, organized, and tested. It also helped me understand Android project structure, user interface design, local database management, and modern Android development practices.\n\nThank you!",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Developer Sign-Off
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Developed & Presented By", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Siddhant Hurule", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                        }
                    }
                }

                // Bottom Action
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Close Presentation", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PresentationSection(
    icon: ImageVector,
    title: String,
    iconColor: Color,
    items: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconColor)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.forEach { item ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("• ", fontWeight = FontWeight.Bold, color = iconColor)
                        Text(item, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
