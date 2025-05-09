# Gradle Clean Build Error Log

```
> Task :app:stripDebugDebugSymbols                                              
Unable to strip the following libraries, packaging them as they are: libandroidx.graphics.path.so.
                                  
> Task :app:stripReleaseDebugSymbols
Unable to strip the following libraries, packaging them as they are: libandroidx.graphics.path.so.
                                                                               
> Task :app:kaptGenerateStubsDebugKotlin                                        
w: Kapt currently doesn't support language version 2.0+. Falling back to 1.9.
                                                                               
> Task :app:kaptGenerateStubsReleaseKotlin                                      
w: Kapt currently doesn't support language version 2.0+. Falling back to 1.9.  
                                                                               
> Task :app:compileDebugKotlin                                                 
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/data/repository/DailyCheckupRepositoryImpl.kt:70:74 Unresolved reference 'PendingOperation'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/data/repository/DailyCheckupRepositoryImpl.kt:85:67 Unresolved reference 'PendingOperation'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/data/repository/DailyCheckupRepositoryImpl.kt:96:55 Unresolved reference 'PendingOperation'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/data/repository/DailyCheckupRepositoryImpl.kt:110:23 Unresolved reference 'DailyCheckupDto'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/data/repository/SprintReviewRepositoryImpl.kt:25:30 Unresolved reference 'SyncManager'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/data/repository/SprintReviewRepositoryImpl.kt:50:34 Unresolved reference 'getCurrentUserId'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/data/repository/SprintReviewRepositoryImpl.kt:61:21 Unresolved reference 'scheduleSync'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/data/repository/SprintReviewRepositoryImpl.kt:75:25 Unresolved reference 'scheduleSync'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/data/repository/SprintReviewRepositoryImpl.kt:103:25 Unresolved reference 'markSynced'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/di/RepositoryModule.kt:100:26 Unresolved reference 'CheckupEntryDao'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/di/RepositoryModule.kt:101:33 Unresolved reference 'CheckupEntryApiService'.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/di/RepositoryModule.kt:108:13 No parameter with name 'checkupEntryDao' found.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/di/RepositoryModule.kt:109:13 No parameter with name 'checkupEntryApiService' found.
e: file:///Users/yassineboulkaid/AndroidStudioProjects/AgileLifeManagement/app/src/main/java/com/example/agilelifemanagement/di/RepositoryModule.kt:120:25 Unresolved reference 'ReviewEntryDao'.
...

> Task :app:compileDebugKotlin FAILED

FAILURE: Build completed with 2 failures.

1: Task failed with an exception.
-----------
* What went wrong:
Execution failed for task ':app:compileReleaseKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
   > Compilation error. See log for more details

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.
==============================================================================

2: Task failed with an exception.
-----------
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
> A failure occurred while executing org.jetbrains.kotlin.compilerRunner.GradleCompilerRunnerWithWorkers$GradleKotlinCompilerWorkAction
   > Compilation error. See log for more details

* Try:
> Run with --stacktrace option to get the stack trace.
> Run with --info or --debug option to get more log output.
> Run with --scan to get full insights.
> Get more help at https://help.gradle.org.
==============================================================================

BUILD FAILED in 1m 23s
73 actionable tasks: 72 executed, 1 up-to-date
```
