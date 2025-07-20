plugins {
    id("com.android.dynamic-feature")
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.myapplication.analytics"
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
    
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            it.finalizedBy(tasks.named("jacocoTestReport"))
        }
    }
    
    testCoverage {
        jacocoVersion = "0.8.11"
    }
    
    tasks.register<JacocoReport>("jacocoTestReport") {
        dependsOn("testDebugUnitTest")
        
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
        
        classDirectories.setFrom(fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
            exclude(
                "**/R.class",
                "**/R\$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*"
            )
        })
        
        sourceDirectories.setFrom(files(
            "${project.projectDir}/src/main/java",
            "${project.projectDir}/src/main/kotlin"
        ))
        
        executionData.setFrom(fileTree(layout.buildDirectory.get()) {
            include("jacoco/testDebugUnitTest.exec")
        })
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":app"))
}