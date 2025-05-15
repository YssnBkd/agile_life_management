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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.agilelifemanagement.ui.components.cards.ExpressiveCard
import com.example.agilelifemanagement.ui.theme.AgileLifeTheme

/**
 * DayWellnessScreen allows users to track their mood, energy, focus, and productivity for the day
 * following Material 3 Expressive design principles.
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
    var energyLevel by remember { mutableIntStateOf(dayData.energyLevel) }
    var focusLevel by remember { mutableIntStateOf(dayData.focusLevel) }
    var productivityLevel by remember { mutableIntStateOf(dayData.productivityLevel) }
    var selectedMood by remember { mutableStateOf(MoodType.PRODUCTIVE) } // Default mood
    var dailyNote by remember { mutableStateOf(dayData.dailyNote ?: "") }
    
    // Top app bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Day Wellness",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
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
                            contentDescription = "Save wellness data"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Day header
            DayHeader(dayData)
            
            // Mood selection
            MoodSelector(
                selectedMood = selectedMood,
                onMoodSelected = { selectedMood = it }
            )
            
            // Wellness metrics
            WellnessMetrics(
                energyLevel = energyLevel,
                onEnergyChange = { energyLevel = it },
                focusLevel = focusLevel,
                onFocusChange = { focusLevel = it },
                productivityLevel = productivityLevel,
                onProductivityChange = { productivityLevel = it }
            )
            
            // Daily note
            DailyNoteInput(
                dailyNote = dailyNote,
                onNoteChange = { dailyNote = it }
            )
            
            // Save button
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Wellness Data",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            // Bottom spacing
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DayHeader(dayData: DayData) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date icon
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = dayData.dayOfWeek,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = dayData.longFormattedDate,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MoodSelector(
    selectedMood: MoodType,
    onMoodSelected: (MoodType) -> Unit
) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "How are you feeling today?",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            MoodType.values().forEach { mood ->
                MoodOption(
                    mood = mood,
                    selected = selectedMood == mood,
                    onClick = { onMoodSelected(mood) }
                )
            }
        }
    }
}

@Composable
private fun MoodOption(
    mood: MoodType,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = if (selected) mood.color.copy(alpha = 0.2f) else Color.Transparent
            )
            .padding(8.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
    ) {
        // Mood emoji with background
        Surface(
            color = if (selected) mood.color else MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = if (selected) Color.White else mood.color,
            shape = CircleShape,
            modifier = Modifier.size(56.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = mood.icon,
                    contentDescription = mood.label,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = mood.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) mood.color else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun WellnessMetrics(
    energyLevel: Int,
    onEnergyChange: (Int) -> Unit,
    focusLevel: Int,
    onFocusChange: (Int) -> Unit,
    productivityLevel: Int,
    onProductivityChange: (Int) -> Unit
) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Wellness Metrics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Energy level slider
        WellnessSlider(
            value = energyLevel,
            onValueChange = onEnergyChange,
            icon = Icons.Rounded.WbSunny,
            iconTint = AgileLifeTheme.extendedColors.accentSunflower,
            label = "Energy Level",
            valueColor = getColorForValue(energyLevel, AgileLifeTheme.extendedColors.accentSunflower)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Focus level slider
        WellnessSlider(
            value = focusLevel,
            onValueChange = onFocusChange,
            icon = Icons.Rounded.SelfImprovement,
            iconTint = AgileLifeTheme.extendedColors.accentLavender,
            label = "Focus Level",
            valueColor = getColorForValue(focusLevel, AgileLifeTheme.extendedColors.accentLavender)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Productivity level slider
        WellnessSlider(
            value = productivityLevel,
            onValueChange = onProductivityChange,
            icon = Icons.Rounded.StarRate,
            iconTint = AgileLifeTheme.extendedColors.accentMint,
            label = "Productivity Level",
            valueColor = getColorForValue(productivityLevel, AgileLifeTheme.extendedColors.accentMint)
        )
    }
}

@Composable
private fun WellnessSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    icon: ImageVector,
    iconTint: Color,
    label: String,
    valueColor: Color
) {
    Column {
        // Label and value
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Label with icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            
            // Value display
            Surface(
                color = valueColor.copy(alpha = 0.2f),
                contentColor = valueColor,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Slider with +/- buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Minus button
            IconButton(
                onClick = { 
                    if (value > 1) onValueChange(value - 1) 
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease"
                )
            }
            
            // Slider
            val sliderPosition by remember { mutableFloatStateOf(value.toFloat()) }
            val animatedPosition by animateFloatAsState(targetValue = value.toFloat(), label = "")
            
            Slider(
                value = animatedPosition,
                onValueChange = { /* Update is handled by buttons */ },
                valueRange = 1f..10f,
                steps = 8,
                modifier = Modifier.weight(1f),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(valueColor),
                        contentAlignment = Alignment.Center
                    ) {
                        // Empty thumb
                    }
                },
                track = {
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedPosition / 10f)
                                .height(8.dp)
                                .clip(CircleShape)
                                .background(valueColor.copy(alpha = 0.7f))
                        )
                    }
                }
            )
            
            // Plus button
            IconButton(
                onClick = { 
                    if (value < 10) onValueChange(value + 1) 
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase"
                )
            }
        }
        
        // Level description
        Text(
            text = getDescriptionForValue(value),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DailyNoteInput(
    dailyNote: String,
    onNoteChange: (String) -> Unit
) {
    ExpressiveCard(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Daily Reflection",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Capture your thoughts, feelings, and reflections about today",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = dailyNote,
            onValueChange = onNoteChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            placeholder = {
                Text("How was your day? What went well? What could be improved?")
            },
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

// Helper functions
private fun getColorForValue(value: Int, baseColor: Color): Color {
    return when {
        value >= 8 -> baseColor
        value >= 5 -> AgileLifeTheme.extendedColors.accentSunflower
        else -> AgileLifeTheme.extendedColors.accentCoral
    }
}

private fun getDescriptionForValue(value: Int): String {
    return when {
        value >= 9 -> "Excellent"
        value >= 7 -> "Very Good"
        value >= 5 -> "Good"
        value >= 3 -> "Fair"
        else -> "Needs Improvement"
    }
}

/**
 * Mood types for tracking daily mood
 */
enum class MoodType(val label: String, val color: Color, val icon: ImageVector) {
    ENERGETIC("Energetic", AgileLifeTheme.extendedColors.accentSunflower, Icons.Rounded.WbSunny),
    PRODUCTIVE("Productive", AgileLifeTheme.extendedColors.accentMint, Icons.Rounded.StarRate),
    FOCUSED("Focused", AgileLifeTheme.extendedColors.accentLavender, Icons.Rounded.SelfImprovement),
    STRESSED("Stressed", AgileLifeTheme.extendedColors.accentCoral, Icons.Rounded.FireExtinguisher),
    CREATIVE("Creative", AgileLifeTheme.extendedColors.accentAqua, Icons.Rounded.Psychology)
}

// Clickable modifier extension (simplified for this example)
fun Modifier.clickable(onClick: () -> Unit): Modifier {
    return this.then(
        androidx.compose.foundation.clickable(
            onClick = onClick,
            indication = null,
            interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()
        )
    )
}
