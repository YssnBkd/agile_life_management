package com.example.agilelifemanagement.ui.screens.day

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.FireExtinguisher
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.StarRate
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.foundation.clickable as foundationClickable
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme
import com.example.agilelifemanagement.ui.theme.AgilePurple
import com.example.agilelifemanagement.ui.theme.WarningOrange
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.agilelifemanagement.ui.model.DayData
import com.example.agilelifemanagement.ui.model.SampleDayData

/**
 * Screen for tracking and editing daily wellness metrics.
 * 
 * @param dayId ID of the day to edit
 * @param onBackClick Callback when back button is pressed
 * @param onSaveClick Callback when save button is pressed
 * @param modifier Optional modifier
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayWellnessScreen(
    dayId: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Sample data - would come from ViewModel in real implementation
    val dayData = remember { SampleDayData.getDay(dayId) }
    
    // State for wellness metrics
    var energy by remember { mutableIntStateOf(dayData.energyLevel) }
    var focus by remember { mutableIntStateOf(dayData.focusLevel) }
    var productivity by remember { mutableIntStateOf(dayData.productivityLevel) }
    var note by remember { mutableStateOf(dayData.dailyNote) }
    var selectedMood by remember { mutableStateOf(MoodType.entries[dayData.mood - 1]) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Wellness") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSaveClick) {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = "Save changes"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Day header
            DayHeader(dayData)
            
            // Mood selector
            MoodSelector(
                selectedMood = selectedMood,
                onMoodSelected = { selectedMood = it }
            )
            
            // Wellness metrics
            WellnessMetrics(
                energyLevel = energy,
                onEnergyChange = { energy = it },
                focusLevel = focus,
                onFocusChange = { focus = it },
                productivityLevel = productivity,
                onProductivityChange = { productivity = it }
            )
            
            // Daily note
            DailyNoteInput(
                dailyNote = note,
                onNoteChange = { note = it }
            )
            
            // Save button
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Wellness Log")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Day header showing date and day of week.
 */
@Composable
fun DayHeader(dayData: DayData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Calendar icon with circular background
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Date information
        Column {
            Text(
                text = dayData.dayOfWeek,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = dayData.longFormattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Mood selector with emoji-style options.
 */
@Composable
fun MoodSelector(
    selectedMood: MoodType,
    onMoodSelected: (MoodType) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MoodType.entries.forEach { mood ->
                MoodOption(
                    mood = mood,
                    selected = mood == selectedMood,
                    onClick = { onMoodSelected(mood) }
                )
            }
        }
    }
}

/**
 * Individual mood option with emoji and selection indicator.
 */
@Composable
fun MoodOption(
    mood: MoodType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        // Emoji circle with background
        Surface(
            shape = CircleShape,
            color = if (selected) mood.getColor() else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mood.emoji,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Mood label
        Text(
            text = mood.label,
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) 
                MaterialTheme.colorScheme.onBackground 
            else 
                MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Wellness metrics sliders for various aspects of daily wellness.
 */
@Composable
fun WellnessMetrics(
    energyLevel: Int,
    onEnergyChange: (Int) -> Unit,
    focusLevel: Int,
    onFocusChange: (Int) -> Unit,
    productivityLevel: Int,
    onProductivityChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Track your wellness",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Energy level
        WellnessSlider(
            value = energyLevel,
            onValueChange = onEnergyChange,
            icon = Icons.Rounded.WbSunny,
            iconTint = Color(0xFFFF9800), // Orange
            label = "Energy Level",
            valueColor = Color(0xFFFF9800)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Focus level
        WellnessSlider(
            value = focusLevel,
            onValueChange = onFocusChange,
            icon = Icons.Rounded.Psychology,
            iconTint = Color(0xFF2196F3), // Blue
            label = "Focus Level",
            valueColor = Color(0xFF2196F3)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Productivity level
        WellnessSlider(
            value = productivityLevel,
            onValueChange = onProductivityChange,
            icon = Icons.Rounded.SelfImprovement,
            iconTint = Color(0xFF4CAF50), // Green
            label = "Productivity Level",
            valueColor = Color(0xFF4CAF50)
        )
    }
}

/**
 * Slider with icon, label, and dynamic color based on value.
 */
@Composable
fun WellnessSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    icon: ImageVector,
    iconTint: Color,
    label: String,
    valueColor: Color
) {
    // Animated value for visual feedback
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat(),
        label = "Animate value"
    )
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        // Header row with icon and label
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Label and value text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = getDescriptionForValue(value),
                    style = MaterialTheme.typography.bodySmall,
                    color = getColorForValue(value, valueColor)
                )
            }
            
            // Numeric value
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(getColorForValue(value, valueColor)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Slider with - and + buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Decrease button
            IconButton(
                onClick = { if (value > 1) onValueChange(value - 1) },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Decrease",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            // Slider
            Slider(
                value = animatedValue,
                onValueChange = { newValue -> onValueChange(newValue.toInt()) },
                valueRange = 1f..5f,
                steps = 3,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = getColorForValue(value, valueColor),
                    activeTrackColor = getColorForValue(value, valueColor).copy(alpha = 0.7f),
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                )
            )
            
            // Increase button
            IconButton(
                onClick = { if (value < 5) onValueChange(value + 1) },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Increase",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Input field for daily reflection notes.
 */
@Composable
fun DailyNoteInput(
    dailyNote: String,
    onNoteChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Daily Reflection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Add notes about your day and reflections on your wellness",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = dailyNote,
            onValueChange = onNoteChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text("What went well today? What could be improved?") },
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

// Helper functions
private fun getColorForValue(value: Int, baseColor: Color): Color {
    val alpha = 0.4f + (value.toFloat() / 5f) * 0.6f
    return baseColor.copy(alpha = alpha)
}

private fun getDescriptionForValue(value: Int): String {
    return when (value) {
        1 -> "Very Low"
        2 -> "Low"
        3 -> "Moderate"
        4 -> "High"
        5 -> "Excellent"
        else -> "Unknown"
    }
}

/**
 * Mood types for tracking daily mood
 */
enum class MoodType(
    val label: String, 
    val emoji: String,
    val value: Int
) {
    TERRIBLE("Terrible", "😖", 1),
    BAD("Bad", "☹️", 2),
    NEUTRAL("Neutral", "😐", 3),
    GOOD("Good", "😊", 4),
    GREAT("Great", "😄", 5);
    
    fun getColor(): Color {
        return when (this) {
            TERRIBLE -> Color(0xFFD50000) // Red
            BAD -> Color(0xFFFF6D00) // Orange
            NEUTRAL -> Color(0xFFFFEB3B) // Yellow
            GOOD -> Color(0xFF43A047) // Green
            GREAT -> Color(0xFF303F9F) // Blue
        }
    }
}

// Extension function for clickable modifier
private fun Modifier.clickable(onClick: () -> Unit): Modifier {
    return this.foundationClickable(
        onClick = onClick,
        indication = null,
        interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()
    )
}
