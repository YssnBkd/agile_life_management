# Component Architecture Plan

## Reusable UI Components Structure
- **Atomic Design:** Organize components by atomic design principles (atoms, molecules, organisms).
- **Component Directory:** Place reusable components in `ui/components/`.
  - Atoms: Buttons, icons, text fields
  - Molecules: Card, list items, dialogs
  - Organisms: Complex widgets (task cards, sprint summaries)
- **Props and State:** Use parameterized composables for reusability and testability.
- **Preview Annotations:** Provide `@Preview` for all reusable components for rapid UI iteration.

## State Management for UI Components
- **Stateless by Default:** Components should be stateless and receive state via parameters.
- **State Hoisting:** Hoist state to ViewModels or parent composables; use `remember` only for local UI state.
- **UI State Models:** Use data classes and sealed classes for representing UI state (loading, error, success).
- **Event Handling:** Pass event lambdas (e.g., `onClick`, `onValueChange`) as parameters.

## Responsive Layout Implementation Strategy
- **Adaptive Layouts:** Use `BoxWithConstraints`, `Modifier.widthIn`, and `ConstraintLayout` for responsive design.
- **Window Size Classes:** Leverage `WindowSizeClass` for multi-device support (phones, tablets, foldables).
- **Orientation Handling:** Use Compose's `LocalConfiguration` to adapt to orientation changes.
- **Minimum Touch Targets:** Ensure all interactive elements are at least 48x48dp.

## Accessibility Considerations
- **Content Descriptions:** All icons and images must have `contentDescription`.
- **Semantic Modifiers:** Use `Modifier.semantics` and `Modifier.clearAndSetSemantics` for custom widgets.
- **Font Scaling:** Support system font scaling and test with large font sizes.
- **Contrast and Focus:** Ensure high-contrast themes and visible focus indicators for keyboard navigation.

## Example: Stateless Button Component
```kotlin
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
```

---

**Next:** Screen implementation plan.
