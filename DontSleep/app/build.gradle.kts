plugins {
    id("com.android.application")
    kotlin("android")
}

val composeCompilerVersion = "1.5.3"

dependencies {
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation(project(mapOf("path" to ":core")))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.10")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("com.google.android.play:core-ktx:1.8.1")
    implementation("androidx.work:work-runtime:2.8.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.compiler:compiler:$composeCompilerVersion")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    //Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

android {
    compileSdk = 34
    defaultConfig {
        applicationId = "com.ilieinc.dontsleep"
        minSdk = 24
        targetSdk = 33
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
        kotlinCompilerExtensionVersion = composeCompilerVersion
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
