# Visual Design System for Agile Life Management

## Color Palette
- **Primary:** #181A54 (as implemented, Material 3 compliant)
- **Dynamic Color:** Supported for Android 12+ (leverages system accent colors)
- **Light/Dark Modes:** Both supported, with appropriate color schemes defined in `Theme.kt` and `Color.kt`.
- **Semantic Colors:** Use semantic naming for color roles (e.g., `primary`, `secondary`, `error`, `background`, `surface`).
- **Accessibility:** Ensure sufficient contrast for all text and UI elements. Test with accessibility tools.

## Typography
- **Font Family:** Inter (as implemented)
- **Weights:** Regular, Medium, SemiBold, Bold
- **Hierarchy:**
  - Display: Large, Medium, Small
  - Headline: Large, Medium, Small
  - Title, Body, Label (see `Type.kt`)
- **Line Height & Spacing:** Use Material 3 recommended spacing for readability and consistency.

## Spacing System
- **Base Unit:** 4dp or 8dp grid system
- **Component Padding:** Use multiples of base unit for padding/margin
- **Consistent Gutter:** Maintain consistent spacing between cards, lists, and sections

## Component Styling
- **Buttons:**
  - Use Material 3 Button styles (Filled, Outlined, Text)
  - Rounded corners (as defined in `Shape.kt`)
  - Stateful styling (hover, pressed, disabled)
- **Cards:**
  - Elevation for hierarchy
  - Rounded corners, shadow for depth
- **Inputs:**
  - Use Material 3 TextField styles
  - Clear focus/active states
- **Navigation:**
  - Bottom navigation with clear active/inactive states
  - Use icons and labels

## Animation and Transition Specs
- **Animation Constants:** Centralized in `Animation.kt`
- **Screen Transitions:** Use Material motion patterns (fade, slide, shared axis)
- **Component Feedback:** Ripple for touch, subtle scale or color for interaction
- **Performance:** Use Compose's built-in animation APIs for smooth, jank-free transitions

## Iconography
- **Material Icons:** Use Material Symbols or Google Fonts icons for consistency
- **Custom Icons:** If needed, follow Material guidelines for size and padding
- **Accessibility:** All icons must have content descriptions for screen readers

## Accessibility Considerations
- **Contrast:** Test color contrast for all states
- **Touch Targets:** Minimum 48x48dp
- **Content Descriptions:** All interactive elements and icons
- **Font Scaling:** Support system font size and scaling

---

**Next:** Component architecture plan.
