plugins {
    id("com.android.application")
    kotlin("android")
}

val composeCompilerVersion = "1.4.4"

dependencies {
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation(project(mapOf("path" to ":core")))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.20")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.activity:activity:1.7.0")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("com.google.android.play:core-ktx:1.8.1")
    implementation("androidx.work:work-runtime:2.8.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.9.0-beta01")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("androidx.compose.ui:ui:1.5.0-alpha01")
    implementation("androidx.compose.ui:ui-tooling:1.4.0")
    implementation("androidx.compose.compiler:compiler:$composeCompilerVersion")
    implementation("androidx.compose.foundation:foundation:1.4.0")
    implementation("androidx.compose.material:material:1.4.0")
    implementation("androidx.compose.material3:material3:1.1.0-beta01")
    implementation("androidx.compose.material:material-icons-core:1.4.0")
    implementation("androidx.compose.material:material-icons-extended:1.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    //Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.ilieinc.dontsleep"
        minSdk = 24
        targetSdk = 33
        versionCode = 25
        versionName = "2.$versionCode"
        multiDexEnabled = true
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true
        // Sets Java compatibility to Java 11
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeCompilerVersion
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug"){
            applicationIdSuffix = "test"
        }
    }
    namespace = "com.ilieinc.dontsleep"
}
