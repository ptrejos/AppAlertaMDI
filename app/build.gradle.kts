plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.appalertamdi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.appalertamdi"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Material Design (versión unificada)
    implementation("com.google.android.material:material:1.11.0")

    // Layout components (eliminé duplicados)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Google Play Services
    implementation(libs.play.services.maps)
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Networking (Retrofit + OkHttp)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.core.ktx)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.espresso.contrib)
    implementation(libs.androidx.rules)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // === TESTING DEPENDENCIES ===

    // Unit Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0") // InstantTaskExecutorRule
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

    // Integration Testing (Espresso)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")

    // Para testing de Activities y Fragments
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")

    // Para mocking en tests de integración (opcional)
    androidTestImplementation("org.mockito:mockito-android:5.12.0")

    // Para testing de LiveData en integration tests
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

  
    // Otras dependencias para las UnitTests

    // JUnit para pruebas básicas
    testImplementation("junit:junit:4.13.2")

    // Mockito para pruebas con mocks
    testImplementation("org.mockito:mockito-core:4.11.0")

    // Robolectric para pruebas Android sin emulador
    testImplementation("org.robolectric:robolectric:4.10.3")

    // AndroidX Test Core (ApplicationProvider, etc.)
    testImplementation("androidx.test:core:1.5.0")

    // Kotlin test
    testImplementation("org.jetbrains.kotlin:kotlin-test")



}