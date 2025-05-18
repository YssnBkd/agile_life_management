import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp) // KSP plugin for annotation processing
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.serialization)
}

// Load local properties
val properties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(localPropertiesFile.reader())
    }
}

// Set properties for use in buildConfig
val supabaseUrl = properties["SUPABASE_URL"] ?: System.getenv("SUPABASE_URL") ?: "REPLACE_ME_SUPABASE_URL"
val supabaseKey = properties["SUPABASE_KEY"] ?: System.getenv("SUPABASE_KEY") ?: "REPLACE_ME_SUPABASE_KEY"
project.ext.set("SUPABASE_URL", supabaseUrl)
project.ext.set("SUPABASE_KEY", supabaseKey)

android {
    namespace = "com.example.agilelifemanagement"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.agilelifemanagement"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject Supabase credentials from local.properties
        buildConfigField("String", "SUPABASE_URL", project.properties["SUPABASE_URL"] as String)
        buildConfigField("String", "SUPABASE_KEY", project.properties["SUPABASE_KEY"] as String)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "35.0.0"
    
    // Room schema export location
    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.play.services.drive)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    
    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.size)
    implementation(libs.androidx.material.icons.extended)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    
    // Navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.supabase.functions)
    
    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // KSP annotation processor for Room
    ksp(libs.androidx.room.compiler)
    // Apply Room schema location

    
    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    // KSP annotation processor for Hilt
    ksp(libs.hilt.compiler)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    // Supabase (updated for 3.0.0 and artifact rename)
    implementation(libs.supabase.auth)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.realtime)
    implementation(libs.supabase.storage)
    
    // Calendar
    implementation(libs.calendar.compose)
    
    // Date/Time
    implementation(libs.threetenabp)
    
    // Image Loading
    implementation(libs.coil.compose)
    
    // Accompanist
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)
    
    // Timber for logging
    implementation(libs.timber)
    // Gson for JSON serialization
    implementation(libs.gson)
    // DataStore for preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}