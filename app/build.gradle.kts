import java.util.Properties
import kotlin.apply

 plugins {
    alias(libs.plugins.kotlin.android)
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
     kotlin("plugin.serialization") version "2.0.21"

     // Add the Google services Gradle plugin
     id("com.google.gms.google-services")

 }



android {
    namespace = "com.example.bunnix"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.bunnix"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties().apply {
            val localFile = rootProject.file("local.properties")
            if (localFile.exists()) {
                load(localFile.inputStream())
            }
        }

        val supabaseUrl = localProperties.getProperty("sbUrl") ?: ""
        val supabaseKey = localProperties.getProperty("sbKey") ?: ""

        buildConfigField("String", "SB_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SB_KEY", "\"$supabaseKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}



dependencies {

    // ===== COMPOSE =====
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // ===== CORE =====
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.3")

    implementation("com.google.android.gms:play-services-maps:18.2.0")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    // ===== LIFECYCLE =====
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // ===== NAVIGATION =====
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // ===== HILT =====
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // ===== ROOM =====
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // ===== NETWORK =====
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ===== COROUTINES =====
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // ===== IMAGE LOADING =====
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ===== DATASTORE =====
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // ===== SPLASH SCREEN =====
    implementation("androidx.core:core-splashscreen:1.0.1")

    // ===== TESTING =====
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Accompanist SwipeRefresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")

//    ============= AUTH BACKEND (Added by [Your Name] - Jan 7, 2026) =============
//    These dependencies are ONLY for auth backend API calls
//            If you're not working on auth, these won't affect you
    implementation("com.squareup.retrofit2:retrofit:2.9.0")                      // Makes API calls
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")                // Converts JSON
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")            // Logs network requests
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")    // Async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")       // Coroutines core

    val supabaseVersion = "3.0.2"

    // Supabase
    implementation("io.github.jan-tennert.supabase:postgrest-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:realtime-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:$supabaseVersion")
    implementation("io.github.jan-tennert.supabase:storage-kt:${supabaseVersion}")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Coroutines Flow
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
//    ============= END AUTH BACKEND =============

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.9.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation("com.google.firebase:firebase-auth")

    // Firebase Services
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // Coroutines for Firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Supabase Core and Modules
    val supabase_version = "2.1.0" // Use the latest stable version
    implementation("io.github.jan-tennert.supabase:postgrest-kt:$supabase_version")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:$supabase_version")
    implementation("io.github.jan-tennert.supabase:storage-kt:$supabase_version")
    implementation("io.github.jan-tennert.supabase:realtime-kt:$supabase_version")

    // Ktor Client (Required by Supabase)
    implementation("io.ktor:ktor-client-android:2.3.7")

    implementation("com.google.maps.android:maps-compose:6.1.0")
}





