plugins {
    id("com.android.application")
    kotlin("android")
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.10")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.activity:activity:1.6.0-alpha01")
    implementation("androidx.activity:activity-compose:1.6.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("com.google.android.play:core-ktx:1.8.1")
    implementation("androidx.work:work-runtime:2.7.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.6.0-beta01")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("androidx.compose.ui:ui:1.2.0-alpha07")
    implementation("androidx.compose.ui:ui-tooling:1.1.1")
    implementation("androidx.compose.compiler:compiler:1.2.0-alpha07")
    implementation("androidx.compose.foundation:foundation:1.1.1")
    implementation("androidx.compose.material:material:1.1.1")
    implementation("androidx.compose.material3:material3:1.0.0-alpha09")
    implementation("androidx.compose.material:material-icons-core:1.1.1")
    implementation("androidx.compose.material:material-icons-extended:1.1.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")
    //Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.ilieinc.dontsleep"
        minSdk = 24
        targetSdk = 31
        versionCode = 17
        versionName = "1.$versionCode"
        multiDexEnabled = true
    }
    buildFeatures {
        dataBinding = true
        compose = true
        viewBinding = true
    }
    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true
        // Sets Java compatibility to Java 8
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.2.0-alpha07"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
