plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

dependencies {
    implementation(libs.work.runtime.ktx)
    implementation(project(":core"))
    implementation(libs.kotlin.stdlib.jdk7)
    implementation(platform(libs.compose.bom))
    implementation(libs.appcompat)
    implementation(libs.activity)
    implementation(libs.activity.compose)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.extensions)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.work.runtime.ktx)
    implementation(libs.cardview)
    implementation(libs.material)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compiler)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.jsoup)
    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "com.ilieinc.dontsleep"
        minSdk = 24
        targetSdk = 35
        versionCode = 29
        versionName = "2.$versionCode"
        multiDexEnabled = true
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        // Sets Java compatibility to Java 11
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompilerVersion.get()
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug") {
            applicationIdSuffix = ".test"
        }
    }
    namespace = "com.ilieinc.dontsleep"
}
