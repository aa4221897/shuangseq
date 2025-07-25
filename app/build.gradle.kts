plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
    id("jacoco")
    id("com.diffplug.spotless") version "6.25.0"
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint()
            .editorConfigOverride(mapOf(
                "max_line_length" to "120",
                "disabled_rules" to "no-wildcard-imports"
            ))
        licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
    }
    format("xml") {
        target("**/*.xml")
        prettier()
            .config(mapOf("parser" to "xml"))
    }
}

android {
    compileSdk = 34
    namespace = "com.example.lotteryprediction"
    
    dynamicFeatures += setOf(
        ":features:analytics",
        ":features:settings"
    )

    buildFeatures {
        viewBinding = true
        dataBinding = true
        compose = true
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            it.finalizedBy(tasks.named("jacocoTestReport"))
        }
    }

    defaultConfig {
        applicationId = "com.example.lotteryprediction"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${layout.buildDirectory.get()}/compose_metrics"
        )
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${layout.buildDirectory.get()}/compose_metrics"
        )
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    
    // 设置覆盖率阈值
    doLast {
        val report = file("${layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        if (report.exists()) {
            val coverage = report.readText()
                .substringAfter("<counter type=\"INSTRUCTION\" missed=\"")
                .substringBefore("\"")
                .toInt()
            val total = report.readText()
                .substringAfter("covered=\"")
                .substringBefore("\"")
                .toInt()
            val coverageRate = total.toDouble() / (coverage + total)
            
            if (coverageRate < 0.8) {
                throw GradleException("代码覆盖率不足80%，当前为${(coverageRate * 100).toInt()}%")
            }
        }
    }
    
    classDirectories.setFrom(
        fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
            exclude(
                "**/R.class",
                "**/R\$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/*Test*.*",
                "android/**/*.*",
                "**/models/**",
                "**/*\$Lambda$*.*",
                "**/*\$inlined$*.*",
                "**/*Composable*.*",
                "**/*Composer*.*",
                "**/*CompositionLocal*.*"
            )
        }
    )
    
    sourceDirectories.setFrom(files(
        "${project.projectDir}/src/main/java",
        "${project.projectDir}/src/main/kotlin"
    ))
    
    executionData.setFrom(fileTree(layout.buildDirectory.get()) {
        include("jacoco/testDebugUnitTest.exec")
    })
}

kapt {
    correctErrorTypes = true
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
    javacOptions {
        option("-Adagger.hilt.disableModulesHaveInstallInCheck=true")
    }
}

dependencies {
    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.swiperefreshlayout)
    
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation("androidx.work:work-testing:${libs.versions.androidx.work}") {
        because("保持与work-runtime版本一致")
    }
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    
    // Network
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    
    // Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.junit)
    
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.fragment.testing)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}