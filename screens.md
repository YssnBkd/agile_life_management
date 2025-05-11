1. Tasks
TasksScreen
Purpose: List, filter, create, edit, and complete tasks.
Key UI Elements:
Task list (with filtering/sorting)
Floating Action Button (FAB) to add a task
Pull-to-refresh
Swipe actions (complete/delete)
Snackbar for feedback
Empty/error/loading states
Navigation: To TaskDetailScreen
TaskDetailScreen
Purpose: View and edit a single task.
Key UI Elements:
Task title, summary, due date, priority, status
Tag chips, dependencies, comments
Edit/delete actions
Save/cancel buttons
Error handling
Navigation: Back to TasksScreen
2. Goals
GoalsScreen
Purpose: List and manage goals, visualize progress.
Key UI Elements:
Goal list (with progress bars)
FAB to add a goal
Filter by category/status
Progress visualization (linear/circular)
Empty/error/loading states
Navigation: To GoalDetailScreen
GoalDetailScreen
Purpose: Detailed goal info, associated tasks, progress tracking.
Key UI Elements:
Goal details (title, summary, deadline, category)
Progress bar/charts
Associated tasks list
Mark as complete/edit/delete
Add task to goal
Error handling
3. Sprints
SprintsScreen
Purpose: List and manage sprints, show active/inactive, summaries.
Key UI Elements:
Sprint list (active/inactive tabs or filter)
Sprint summary cards (progress, dates, status)
FAB to add a sprint
Empty/error/loading states
Navigation: To SprintDetailScreen
SprintDetailScreen
Purpose: View sprint backlog, progress, and review.
Key UI Elements:
Sprint info (name, summary, dates, status)
Backlog/task list for sprint
Progress bar
Start/end sprint actions
Link to SprintReviewScreen
Error handling
4. Daily Checkup
DailyCheckupScreen
Purpose: Quick daily review, habit tracking, notes.
Key UI Elements:
Habit checklist
Notes input
Submit/save button
Previous checkup summary
Error/loading states
5. Sprint Review
SprintReviewScreen
Purpose: Rate and reflect on completed sprints.
Key UI Elements:
Sprint summary
Rating (1–5 stars or similar)
Text reflection input
Submit/save button
Error/loading states
6. Settings
SettingsScreen
Purpose: Profile, theme, notification preferences, integrations.
Key UI Elements:
Profile info (avatar, name, email)
Theme toggle (light/dark/dynamic)
Notification settings
Integration management (e.g., Supabase, Google)
Logout button
Additional Notes:
All screens:
Should support loading, error, and empty states using sealed UI state classes.
Should be fully accessible (content descriptions, font scaling, focus order).
Should use bottom navigation for Tasks, Goals, Sprints, and Settings as primary destinations.
Detail screens should be accessible via deep links and navigation actions.