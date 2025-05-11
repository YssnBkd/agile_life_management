# Agile Life Management

A modern Android application that brings agile project management principles to personal life management. This app helps users organize their tasks, goals, and activities using sprints, daily check-ins, and retrospectives - just like in professional agile environments.

## 📱 Screenshots

[Screenshots to be added]

## ✨ Features

- **User Authentication**: Secure login and registration using Supabase
- **Sprint Management**: Create, track, and complete time-boxed work periods
- **Task Management**: Organize tasks with priorities, statuses, and dependencies
- **Goal Tracking**: Set and monitor long-term goals with categories
- **Daily Check-ups**: Record daily progress and blockers
- **Sprint Reviews**: Reflect on completed sprints with ratings and insights
- **Calendar View**: Visualize all tasks and deadlines in a calendar interface
- **Tagging System**: Categorize tasks, sprints, and goals with customizable tags
- **Notifications**: Get reminders for upcoming deadlines and tasks

## 🏗️ Architecture

The app follows **Clean Architecture** principles with **MVVM** pattern:

- **Presentation Layer**: Jetpack Compose UI components and ViewModels
- **Domain Layer**: Business logic with use cases, models, and repository interfaces
- **Data Layer**: Repository implementations, local and remote data sources

Key architectural components:
- **Repository Pattern**: Abstracts data sources from the business logic
- **Use Cases**: Encapsulate business rules and operations
- **Dependency Injection**: Modular and testable components using Hilt
- **Reactive Data Flow**: UI updates reactively to data changes

## 🛠️ Technologies & Libraries

- **UI**: Jetpack Compose with Material 3 design
- **Architecture Components**: ViewModel, LiveData, Room
- **Dependency Injection**: Hilt
- **Navigation**: Jetpack Navigation Compose
- **Networking**: Ktor Client
- **Local Database**: Room
- **Backend Services**: Supabase (Auth, PostgreSQL, Storage)
- **Asynchronous Programming**: Kotlin Coroutines and Flow
- **Date/Time**: ThreeTenABP
- **Image Loading**: Coil
- **Logging**: Timber
- **JSON Serialization**: Gson
- **CI/CD**: GitHub Actions

## 📋 Prerequisites

- Android Studio Arctic Fox or later
- JDK 11
- Android SDK 35
- Supabase account and project

## ⚙️ Installation

1. Clone the repository
```bash
git clone https://github.com/yourusername/AgileLifeManagement.git
```

2. Configure Supabase credentials
   - Create a `local.properties` file in the project root if it doesn't exist
   - Add your Supabase credentials:
   ```
   SUPABASE_URL=your_supabase_url
   SUPABASE_KEY=your_supabase_key
   ```

3. Open the project in Android Studio

4. Sync Gradle and build the project

5. Run on an emulator or physical device

## 🚀 Usage

After launching the app:

1. Create an account or log in
2. Set up your first sprint by specifying a duration and objectives
3. Add tasks to your sprint
4. Use the daily check-up feature to track progress
5. Complete tasks as you work through them
6. Conduct a sprint review when finished
7. View your progress in the calendar or dedicated sections

## 🛣️ Roadmap

### Implemented
- User authentication flows
- Core sprint, task, and goal management
- Local database persistence
- Supabase integration for backend services
- Material 3 UI with dark/light themes

### Coming Soon
- Data synchronization improvements
- Offline support with automatic sync
- Advanced analytics and reporting
- Team collaboration features
- Custom notification settings
- Export/import functionality
- Desktop companion app

## 🔐 Security Implementation
- Data encryption for sensitive information
- Secure storage of credentials
- API security best practices
- Input validation and sanitization

## 💻 CI/CD Pipeline
- GitHub Actions for automated builds
- Static code analysis integration
- Automated testing on PR submissions
- Release automation and versioning strategy

## 🤝 Contributing

Contributions, issues and feature requests are welcome. Feel free to check the [issues page](https://github.com/yourusername/AgileLifeManagement/issues) if you want to contribute.

## 📄 License

[MIT License](LICENSE)