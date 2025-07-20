plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    kotlin("kapt") version "1.9.22"
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
    // Core模块
    implementation(project(":core"))
    
    // DeepSeek SDK
    implementation(libs.deepseek.sdk)
    
    // Hilt依赖
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    
    // kapt配置
    kaptTest(libs.jmh.generator.annprocess)
    kaptAndroidTest(libs.androidx.room.compiler)
    
    // 测试依赖 - 统一版本号
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.junit)
    // 移除重复的mockito-android依赖
    // 确保work-testing版本与work-runtime一致
    testImplementation("androidx.work:work-testing:2.9.0") {
        because("保持与work-runtime版本一致")
    }
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.fragment.testing)
    
    // 基础依赖
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material)
    
    // 新增UI相关依赖
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    
    // 网络相关
    implementation(libs.retrofit)
    implementation("com.squareup.retrofit2:converter-gson:${libs.versions.retrofit.get()}")
    implementation(libs.okhttp)
    
    // 数据库
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    
    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    
    // SwipeRefreshLayout
    implementation(libs.androidx.swiperefreshlayout)
    
    // Lifecycle组件
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    
    // Hilt扩展
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    
    // 基础架构组件
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Compose依赖 (使用BOM管理版本)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    androidTestImplementation(platform(libs.androidx.compose.bom))  // 测试也需要BOM
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}