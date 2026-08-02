package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

enum class WeekDay(val shortName: String) {
    MON("Mon"), TUE("Tue"), WED("Wed"), THU("Thu"), FRI("Fri"), SAT("Sat"), SUN("Sun")
}

data class TimeOfDay(val hour: Int, val minute: Int) {
    val inMinutes: Int get() = hour * 60 + minute
}

data class StudySession(
    val id: String,
    val title: String,
    val day: WeekDay,
    val startTime: TimeOfDay,
    val endTime: TimeOfDay,
    val color: Color
)

@Composable
fun WeeklyCalendarView(
    sessions: List<StudySession>,
    modifier: Modifier = Modifier,
    dayWidth: Dp = 80.dp,
    hourHeight: Dp = 64.dp,
    timeColumnWidth: Dp = 50.dp
) {
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        // Header (Days of week)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
        ) {
            Spacer(modifier = Modifier.width(timeColumnWidth))
            WeekDay.entries.forEach { day ->
                Box(
                    modifier = Modifier
                        .width(dayWidth)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.shortName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Grid Area (Time Column + Schedule Grid)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Time Column
            Column(
                modifier = Modifier
                    .width(timeColumnWidth)
                    .verticalScroll(verticalScrollState)
            ) {
                for (hour in 0..24) {
                    Box(
                        modifier = Modifier
                            .height(hourHeight)
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        if (hour < 24) {
                            Text(
                                text = "${hour.toString().padStart(2, '0')}:00",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Main Grid
            val gridColor = MaterialTheme.colorScheme.surfaceVariant
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
                    .drawBehind {
                        // Draw horizontal lines
                        for (hour in 0..24) {
                            val y = hour * hourHeight.toPx()
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1f
                            )
                        }
                        // Draw vertical lines
                        for (day in 0..7) {
                            val x = day * dayWidth.toPx()
                            drawLine(
                                color = gridColor,
                                start = Offset(x, 0f),
                                end = Offset(x, size.height),
                                strokeWidth = 1f
                            )
                        }
                    }
            ) {
                // Sessions Layout
                Layout(
                    content = {
                        sessions.forEach { session ->
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(session.color)
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = session.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .width(dayWidth * 7)
                        .height(hourHeight * 24)
                ) { measurables, constraints ->
                    val width = (dayWidth * 7).roundToPx()
                    val height = (hourHeight * 24).roundToPx()

                    layout(width, height) {
                        measurables.forEachIndexed { index, placeable ->
                            val session = sessions[index]
                            val startMinutes = session.startTime.inMinutes
                            val endMinutes = session.endTime.inMinutes
                            val durationMinutes = maxOf(endMinutes - startMinutes, 15) // minimum visual height of 15 mins

                            val itemWidth = dayWidth.roundToPx()
                            val itemHeight = ((durationMinutes / 60f) * hourHeight.toPx()).roundToInt()

                            val p = placeable.measure(
                                constraints.copy(
                                    minWidth = itemWidth,
                                    maxWidth = itemWidth,
                                    minHeight = itemHeight,
                                    maxHeight = itemHeight
                                )
                            )

                            val x = session.day.ordinal * itemWidth
                            val y = ((startMinutes / 60f) * hourHeight.toPx()).roundToInt()

                            p.placeRelative(x = x, y = y)
                        }
                    }
                }
            }
        }
    }
}
