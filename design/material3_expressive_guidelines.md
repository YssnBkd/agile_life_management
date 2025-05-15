# Material 3 Expressive UI Guidelines

## Overview

Material 3 Expressive is an extension of Material 3 that allows for more creative freedom and customization while maintaining the core principles of Material Design. This document provides guidelines and implementation details for incorporating Material 3 Expressive into Android applications using Jetpack Compose.

## Key Concepts

### 1. Dynamic Color and Theming

Material 3 Expressive builds upon Material 3's dynamic color system but allows for more customization:

- **Custom Color Schemes**: Create unique brand expressions while maintaining accessibility and coherence
- **Extended Color Palettes**: Access to additional tonal ranges beyond the standard Material 3 palette
- **Color Role Mapping**: Redefine how colors are applied to various UI components

### 2. Typography System

- **Custom Type Scales**: Define unique typography scales that reflect brand identity
- **Variable Fonts Support**: Utilize variable fonts for more expressive typography
- **Typography Role Mapping**: Customize how type styles are applied to different UI elements

### 3. Shape System

- **Custom Shape Scales**: Define unique corner styles and dimensions
- **Component-Specific Shapes**: Apply different shape treatments to specific components
- **Shape Role Mapping**: Redefine how shapes are applied across the UI

### 4. Animation and Motion

- **Custom Motion Patterns**: Define unique animation patterns for transitions and interactions
- **Expressive Transitions**: Create more dynamic and engaging transitions between screens and states
- **Interactive Feedback**: Enhance user feedback through custom motion designs

## Implementation in Jetpack Compose

### Setting Up Material 3 Expressive Theme

```kotlin
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Determine the color scheme
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> customDarkColorScheme
        else -> customLightColorScheme
    }

    // Custom typography
    val typography = customTypography

    // Custom shapes
    val shapes = customShapes

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
```

### Custom Color Schemes

```kotlin
// Light theme custom colors
val customLightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005E),
    secondary = Color(0xFF625B71),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1E192B),
    tertiary = Color(0xFF7E5260),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD9E3),
    onTertiaryContainer = Color(0xFF31101D),
    // Add other colors as needed
)

// Dark theme custom colors
val customDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF371E73),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD9E3),
    // Add other colors as needed
)
```

### Custom Typography

```kotlin
val customTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    // Define other text styles
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    // Add other typography styles as needed
)
```

### Custom Shapes

```kotlin
val customShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)
```

## Component Customization

### Buttons

```kotlin
@Composable
fun ExpressiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = contentPadding,
        content = content
    )
}
```

### Cards

```kotlin
@Composable
fun ExpressiveCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content
    )
}
```

### Text Fields

```kotlin
@Composable
fun ExpressiveTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        shape = MaterialTheme.shapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}
```

## Animation Guidelines

### Transition Animations

```kotlin
// Define custom enter/exit transitions
val customEnterTransition = fadeIn(
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + expandIn(
    animationSpec = tween(300, easing = FastOutSlowInEasing),
    expandFrom = Alignment.Center
)

val customExitTransition = fadeOut(
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + shrinkOut(
    animationSpec = tween(300, easing = FastOutSlowInEasing),
    shrinkTowards = Alignment.Center
)

// Apply to navigation
NavHost(
    navController = navController,
    startDestination = "home",
    enterTransition = { customEnterTransition },
    exitTransition = { customExitTransition }
) {
    // Navigation graph
}
```

### Interactive Animations

```kotlin
@Composable
fun AnimatedButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    Button(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
    ) {
        Text("Animated Button")
    }
}
```

## Best Practices

1. **Maintain Accessibility**: Ensure all customizations maintain proper contrast ratios and touch target sizes
2. **Consistent Application**: Apply expressive elements consistently across the application
3. **Progressive Enhancement**: Start with standard Material 3 components and progressively enhance them
4. **Performance Considerations**: Be mindful of performance impacts when implementing complex animations or effects
5. **Responsive Design**: Ensure expressive elements adapt appropriately to different screen sizes and orientations
6. **Brand Alignment**: Customize elements to align with brand identity while maintaining usability

## Resources

- [Material 3 Documentation](https://m3.material.io/)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material Design Guidelines](https://material.io/design)
- [Compose Animation Documentation](https://developer.android.com/jetpack/compose/animation)
