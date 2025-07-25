plugins {
    id("com.android.dynamic-feature")
    alias(libs.plugins.kotlin.android)
    id("jacoco")
}

apply(from = "../shared-test-config.gradle.kts")

android {
    namespace = "com.example.lotteryprediction.analytics"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":app"))
}