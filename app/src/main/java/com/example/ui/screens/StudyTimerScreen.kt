package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenAccent
import com.example.ui.viewmodel.StudyMateViewModel
import com.example.util.NotificationHelper
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyTimerScreen(
    viewModel: StudyMateViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedMinutes by remember { mutableIntStateOf(25) }
    var totalSeconds by remember { mutableIntStateOf(25 * 60) }
    var secondsLeft by remember { mutableIntStateOf(25 * 60) }
    var isRunning by remember { mutableStateOf(false) }
    var isBreakMode by remember { mutableStateOf(false) }
    var completedSessions by remember { mutableIntStateOf(3) }
    var totalMinutesStudied by remember { mutableIntStateOf(75) }

    // Countdown Timer Loop
    LaunchedEffect(isRunning, secondsLeft) {
        if (isRunning && secondsLeft > 0) {
            delay(1000L)
            secondsLeft -= 1
        } else if (isRunning && secondsLeft == 0) {
            isRunning = false
            if (!isBreakMode) {
                // Completed focus session
                NotificationHelper(context).showNotification(
                    title = "Study Session Complete!",
                    message = "Great job! Time for a short break."
                )
                completedSessions += 1
                totalMinutesStudied += selectedMinutes
                isBreakMode = true
                selectedMinutes = 5
                totalSeconds = 5 * 60
                secondsLeft = 5 * 60
            } else {
                // Completed break session
                NotificationHelper(context).showNotification(
                    title = "Break Over!",
                    message = "Ready to focus? Your next study session awaits."
                )
                isBreakMode = false
                selectedMinutes = 25
                totalSeconds = 25 * 60
                secondsLeft = 25 * 60
            }
        }
    }

    val progress = if (totalSeconds > 0) (secondsLeft.toFloat() / totalSeconds.toFloat()) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "Progress"
    )

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pomodoro Study Timer", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Mode Banner
            Surface(
                color = if (isBreakMode) GreenAccent.copy(alpha = 0.15f) else BluePrimary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isBreakMode) Icons.Default.Coffee else Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (isBreakMode) GreenAccent else BluePrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBreakMode) "Break Time - Take a rest" else "Study Focus Mode",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBreakMode) GreenAccent else BluePrimary
                    )
                }
            }

            // Circular Progress Indicator & Timer Text
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp)
                    .padding(12.dp)
            ) {
                val primaryColor = if (isBreakMode) GreenAccent else BluePrimary
                val trackColor = MaterialTheme.colorScheme.surfaceVariant

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 18.dp.toPx()
                    drawCircle(
                        color = trackColor,
                        style = Stroke(width = strokeWidth)
                    )
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRunning) "Remaining" else "Paused",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Preset Duration Buttons (15, 25, 45, 60 mins)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(15, 25, 45, 60).forEach { mins ->
                    val isSelected = selectedMinutes == mins && !isBreakMode
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (!isRunning) {
                                isBreakMode = false
                                selectedMinutes = mins
                                totalSeconds = mins * 60
                                secondsLeft = mins * 60
                            }
                        },
                        label = { Text("${mins}m") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Timer Controls (Start / Pause, Reset)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isRunning = false
                        secondsLeft = totalSeconds
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = { isRunning = !isRunning },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBreakMode) GreenAccent else BluePrimary
                    ),
                    modifier = Modifier.height(64.dp).weight(1f)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "PAUSE" else "START STUDY",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Study Stats Row
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$completedSessions",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                        Text(
                            text = "Sessions Today",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(36.dp)
                            .width(1.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${totalMinutesStudied} mins",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenAccent
                        )
                        Text(
                            text = "Total Focus Time",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
