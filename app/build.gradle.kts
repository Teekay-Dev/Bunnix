import java.util.Properties
import kotlin.apply

 plugins {
//    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.compose)
//    id("com.google.dagger.hilt.android")
    id("com.android.application")
    //id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
//    id("kotlin-kapt")
//    kotlin("kapt")
//    kotlin("plugin.serialization")
//    id("de.mannodermaus.android-junit5")
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
//    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("io.coil-kt:coil-compose:2.7.0")

    // NEW: Lifecycle & ViewModel for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // NEW: Hilt for Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // NEW: Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // NEW: OkHttp for logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // NEW: Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

//    // NEW: Coil for image loading
//    implementation("io.coil-kt:coil-compose:2.5.0")


    // Material Icons Extended (for more icons)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")


    // Navigation Compose (add this with other dependencies)
    implementation("androidx.navigation:navigation-compose:2.7.6")


    //Pull to refresh material 3
    implementation("androidx.compose.material3:material3:1.3.0")

//    implementation(platform("androidx.compose:compose-bom:2024.02.00"))

    implementation("com.google.accompanist:accompanist-swiperefresh:0.34.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

}



