// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
//    ext.kotlin_version = "1.6.10"
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0-alpha04")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}