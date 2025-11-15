pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.7.2"
        id("org.jetbrains.kotlin.android") version "1.9.24"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // fallback mirror nếu mạng VN yếu
        maven { url = uri("https://maven.google.com/") }
        maven { url = uri("https://maven.aliyun.com/repository/google") }
    }
}

rootProject.name = "foodapp"
include(":app")
