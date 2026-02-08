plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  id("com.google.devtools.ksp") version "2.0.21-1.0.28"
  id("com.google.dagger.hilt.android") version "2.52"
}

android {
  namespace = "com.ruhaan.accolade"
  compileSdk { version = release(36) }

  defaultConfig {
    applicationId = "com.ruhaan.accolade"
    minSdk = 29
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    buildConfigField(
        "String",
        "TMDB_API_KEY",
        "\"eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmZGY5OGFjNGRlODM2MzJiODE1NDI2MjBiNWVhMTgwNCIsIm5iZiI6MTc1MzUxNjUyMi4zMjgsInN1YiI6IjY4ODQ4OWVhODA3YWJkNzU4M2Q5YTdmNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.6Aqi0KanBeUBuEIxkI3UHIu_0KHh3VLbmrAeempn34w\"",
    )
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }

  buildFeatures {
    buildConfig = true
    compose = true
  }

}

dependencies {
  // Navigation
  implementation("androidx.navigation:navigation-compose:2.8.3")

  // Image loading
  implementation("io.coil-kt:coil-compose:2.7.0")

  // Hilt
  implementation("com.google.dagger:hilt-android:2.52")
  ksp("com.google.dagger:hilt-compiler:2.52")
  implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

  // Retrofit + Network
  implementation("com.squareup.retrofit2:retrofit:2.11.0")
  implementation("com.squareup.retrofit2:converter-gson:2.11.0")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

  // Coroutines
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

  // ViewModel
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.tooling)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
}
