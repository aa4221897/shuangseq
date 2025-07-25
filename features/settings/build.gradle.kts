plugins {
    id("com.android.dynamic-feature")
    alias(libs.plugins.kotlin.android)
    id("jacoco")
}

apply(from = "../shared-test-config.gradle.kts")

android {
    namespace = "com.example.lotteryprediction.settings"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":app"))
    implementation(libs.androidx.preference)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}