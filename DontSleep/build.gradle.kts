// Top-level build file for shared configuration across all modules

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false // if Compose plugin is used globally
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
