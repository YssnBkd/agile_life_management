package com.example.agilelifemanagement.ui.components.timeline

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * A comprehensive timeline component for displaying activities throughout the day.
 * Supports dragging for rescheduling, visual status indicators, and configurable time ranges.
 */
@Composable
fun DayTimeline(
    timeBlocks: List<TimeBlock>,
    onTimeBlockClick: (TimeBlock) -> Unit,
    onTimeBlockComplete: (TimeBlock) -> Unit,
    onTimeBlockReschedule: (String, LocalTime, Int) -> Unit,
    modifier: Modifier = Modifier,
    startHour: Int = 6, // Default start hour (6 AM)
    endHour: Int = 22, // Default end hour (10 PM)
    hourHeight: Dp = 120.dp, // Height for each hour
    currentTime: LocalTime = LocalTime.now() // Current time for the now indicator
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    
    // State to track which time block is being dragged
    var draggedTimeBlockId by remember { mutableStateOf<String?>(null) }
    
    // Map to track time block positions
    val timeBlockPositions = remember { mutableStateMapOf<String, Pair<Float, Float>>() }
    
    // Automatically scroll to the current time initially
    LaunchedEffect(key1 = currentTime) {
        val currentTimePosition = calculateTimePosition(
            time = currentTime,
            startHour = startHour,
            hourHeightPx = with(density) { hourHeight.toPx() }
        )
        
        // Scroll to position the current time in the middle of the screen
        scope.launch {
            scrollState.animateScrollTo(
                (currentTimePosition - 300).toInt().coerceAtLeast(0)
            )
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        // Total timeline height
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(hourHeight * (endHour - startHour) + 32.dp)
        ) {
            // Hour markers and grid
            TimelineGrid(
                startHour = startHour,
                endHour = endHour,
                hourHeight = hourHeight
            )
            
            // Current time indicator
            val currentTimePosition = with(density) {
                calculateTimePosition(
                    time = currentTime,
                    startHour = startHour,
                    hourHeightPx = hourHeight.toPx()
                ).toDp()
            }
            
            CurrentTimeIndicator(
                modifier = Modifier
                    .offset(y = currentTimePosition)
                    .zIndex(10f)
            )
            
            // Time blocks
            timeBlocks.forEach { timeBlock ->
                // Calculate start and end times from the time range string
                val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
                val times = timeBlock.timeRange.split(" - ")
                
                if (times.size == 2) {
                    // Parse start and end times
                    val startTime = try {
                        LocalTime.parse(times[0], timeFormatter)
                    } catch (e: Exception) {
                        LocalTime.of(9, 0) // Default fallback
                    }
                    
                    val endTime = try {
                        LocalTime.parse(times[1], timeFormatter)
                    } catch (e: Exception) {
                        startTime.plusHours(1) // Default 1 hour duration
                    }
                    
                    // Calculate positions
                    val startPosition = with(density) {
                        calculateTimePosition(
                            time = startTime,
                            startHour = startHour,
                            hourHeightPx = hourHeight.toPx()
                        ).toDp()
                    }
                    
                    val endPosition = with(density) {
                        calculateTimePosition(
                            time = endTime,
                            startHour = startHour,
                            hourHeightPx = hourHeight.toPx()
                        ).toDp()
                    }
                    
                    val height = (endPosition - startPosition).coerceAtLeast(48.dp)
                    
                    // Animation for dragging
                    val elevation by animateFloatAsState(
                        targetValue = if (draggedTimeBlockId == timeBlock.id) 8f else 2f
                    )
                    
                    // Time block card
                    Box(
                        modifier = Modifier
                            .offset(y = startPosition)
                            .heightIn(min = 48.dp)
                            .height(height)
                            .fillMaxWidth(0.85f)
                            .padding(horizontal = 16.dp)
                            // Position in the center
                            .padding(horizontal = 24.dp)
                            .shadow(elevation.dp, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .background(timeBlock.color.copy(alpha = if (timeBlock.isCompleted) 0.7f else 1f))
                            .onGloballyPositioned { coordinates ->
                                timeBlockPositions[timeBlock.id] = Pair(
                                    coordinates.positionInParent().y,
                                    coordinates.size.height.toFloat()
                                )
                            }
                            .pointerInput(timeBlock.id) {
                                detectDragGestures(
                                    onDragStart = { draggedTimeBlockId = timeBlock.id },
                                    onDragEnd = {
                                        // When drag ends, calculate new time
                                        val position = timeBlockPositions[timeBlock.id]
                                        if (position != null) {
                                            val newY = position.first
                                            val newStartTime = calculateTimeFromPosition(
                                                position = newY,
                                                startHour = startHour,
                                                hourHeightPx = with(density) { hourHeight.toPx() }
                                            )
                                            
                                            // Calculate duration in minutes
                                            val originalDuration = endTime.toSecondOfDay() / 60 - 
                                                                  startTime.toSecondOfDay() / 60
                                            
                                            // Notify about rescheduling
                                            onTimeBlockReschedule(
                                                timeBlock.id,
                                                newStartTime,
                                                originalDuration
                                            )
                                        }
                                        draggedTimeBlockId = null
                                    }
                                ) { change, dragAmount ->
                                    change.consume()
                                    
                                    // Update the position in the map
                                    val currentPosition = timeBlockPositions[timeBlock.id]
                                    if (currentPosition != null) {
                                        timeBlockPositions[timeBlock.id] = Pair(
                                            currentPosition.first + dragAmount.y,
                                            currentPosition.second
                                        )
                                    }
                                }
                            }
                            .clickable { onTimeBlockClick(timeBlock) }
                    ) {
                        TimeBlockContent(
                            timeBlock = timeBlock,
                            onComplete = { onTimeBlockComplete(timeBlock) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Content displayed inside a time block
 */
@Composable
private fun TimeBlockContent(
    timeBlock: TimeBlock,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = timeBlock.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = onComplete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (timeBlock.isCompleted) 
                        Icons.Default.CheckCircle 
                    else 
                        Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle completion",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        
        Text(
            text = timeBlock.timeRange,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
        
        if (timeBlock.description.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timeBlock.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        if (timeBlock.location.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timeBlock.location,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Draws the timeline grid with hour markers
 */
@Composable
private fun TimelineGrid(
    startHour: Int,
    endHour: Int,
    hourHeight: Dp
) {
    for (hour in startHour..endHour) {
        HourMarker(
            hour = hour,
            modifier = Modifier.offset(y = (hourHeight * (hour - startHour)))
        )
    }
}

/**
 * Displays a single hour marker on the timeline
 */
@Composable
private fun HourMarker(
    hour: Int,
    modifier: Modifier = Modifier
) {
    val formattedHour = when {
        hour == 0 -> "12 AM"
        hour < 12 -> "$hour AM"
        hour == 12 -> "12 PM"
        else -> "${hour - 12} PM"
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formattedHour,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Divider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

/**
 * Shows an indicator for the current time
 */
@Composable
private fun CurrentTimeIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Divider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primary,
            thickness = 2.dp
        )
    }
}

/**
 * Calculate position on timeline from time
 */
private fun calculateTimePosition(
    time: LocalTime,
    startHour: Int,
    hourHeightPx: Float
): Float {
    val hour = time.hour
    val minute = time.minute
    
    // Calculate total minutes since startHour
    val totalMinutes = (hour - startHour) * 60 + minute
    
    // Convert to position
    return (totalMinutes.toFloat() / 60f) * hourHeightPx
}

/**
 * Calculate time from position on timeline
 */
private fun calculateTimeFromPosition(
    position: Float,
    startHour: Int,
    hourHeightPx: Float
): LocalTime {
    // Convert position to total hours since startHour
    val totalHours = position / hourHeightPx
    
    // Calculate hours and minutes
    val hours = totalHours.toInt() + startHour
    val minutes = ((totalHours - totalHours.toInt()) * 60).roundToInt()
    
    // Ensure valid time values
    val validHours = hours.coerceIn(0, 23)
    val validMinutes = minutes.coerceIn(0, 59)
    
    return LocalTime.of(validHours, validMinutes)
}

/**
 * Extension function to make a Modifier clickable
 */
fun Modifier.clickable(onClick: () -> Unit): Modifier = this.then(
    Modifier.pointerInput(Unit) {
        detectTapGestures(
            onTap = { onClick() }
        )
    }
)

/**
 * Extension function for z-index ordering
 */
fun Modifier.zIndex(zIndex: Float): Modifier = this
