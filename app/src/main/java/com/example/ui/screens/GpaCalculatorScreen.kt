package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.PriorityHigh
import com.example.ui.viewmodel.StudyMateViewModel

data class GradeSubjectRow(
    val id: Int,
    var name: String = "",
    var marksObtained: String = "",
    var maxMarks: String = "100",
    var credits: String = "3"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaCalculatorScreen(
    viewModel: StudyMateViewModel,
    onBack: () -> Unit
) {
    var rows by remember {
        mutableStateOf(
            listOf(
                GradeSubjectRow(1, "Mathematics", "88", "100", "4"),
                GradeSubjectRow(2, "Computer Science", "92", "100", "4"),
                GradeSubjectRow(3, "Physics", "81", "100", "3"),
                GradeSubjectRow(4, "English Literature", "85", "100", "2")
            )
        )
    }

    var nextId by remember { mutableIntStateOf(5) }

    // Computations
    val totalObtained = rows.sumOf { it.marksObtained.toDoubleOrNull() ?: 0.0 }
    val totalMax = rows.sumOf { it.maxMarks.toDoubleOrNull() ?: 100.0 }
    val overallPercentage = if (totalMax > 0) (totalObtained / totalMax) * 100.0 else 0.0
    val cgpaTenScale = (overallPercentage / 9.5).coerceAtMost(10.0)
    val gpaFourScale = (overallPercentage / 25.0).coerceAtMost(4.0)

    val gradeLetter = when {
        overallPercentage >= 90 -> "A+"
        overallPercentage >= 80 -> "A"
        overallPercentage >= 70 -> "B"
        overallPercentage >= 60 -> "C"
        overallPercentage >= 50 -> "D"
        else -> "F"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GPA & Grade Calculator", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calculated Results Header Card
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BluePrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Estimated Overall Performance",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = String.format("%.2f%%", overallPercentage),
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("CGPA (10.0)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(String.format("%.2f", cgpaTenScale), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Divider(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(1.dp),
                                color = Color.White.copy(alpha = 0.3f)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("GPA (4.0)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(String.format("%.2f", gpaFourScale), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Divider(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(1.dp),
                                color = Color.White.copy(alpha = 0.3f)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Grade", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                Text(gradeLetter, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenAccent)
                            }
                        }
                    }
                }
            }

            // Subject Rows Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Subject Grades & Marks", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Button(
                        onClick = {
                            rows = rows + GradeSubjectRow(nextId, "Subject $nextId", "80", "100", "3")
                            nextId += 1
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Subject", fontSize = 12.sp)
                    }
                }
            }

            // Dynamic Subject Rows
            itemsIndexed(rows, key = { _, row -> row.id }) { index, row ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Subject #${index + 1}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                            if (rows.size > 1) {
                                IconButton(
                                    onClick = { rows = rows.filter { it.id != row.id } },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PriorityHigh, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = row.name,
                            onValueChange = { newName ->
                                rows = rows.map { if (it.id == row.id) it.copy(name = newName) else it }
                            },
                            label = { Text("Subject Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = row.marksObtained,
                                onValueChange = { newObtained ->
                                    rows = rows.map { if (it.id == row.id) it.copy(marksObtained = newObtained) else it }
                                },
                                label = { Text("Marks Obtained") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = row.maxMarks,
                                onValueChange = { newMax ->
                                    rows = rows.map { if (it.id == row.id) it.copy(maxMarks = newMax) else it }
                                },
                                label = { Text("Max Marks") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }
                    }
                }
            }
        }
    }
}
