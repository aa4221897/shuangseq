plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    id("jacoco")
    id("org.sonarqube") version "4.4.1.3373"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        // 添加DeepSeek私有仓库
        maven {
            url = uri("https://your.deepseek.repo.com/repository/maven-public/")
        }
    }
    apply(plugin = "jacoco")
    
    tasks.withType<Test> {
        configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
    
    afterEvaluate {
        if (plugins.hasPlugin("com.android.application") || 
            plugins.hasPlugin("com.android.dynamic-feature")) {
            extensions.configure<JacocoPluginExtension> {
                toolVersion = "0.8.11"
            }
        }
    }
}

task<JacocoReport>("jacocoRootReport") {
    dependsOn(subprojects.map { ":${it.name}:testDebugUnitTest" })
    
    additionalSourceDirs.setFrom(files(subprojects.flatMap { 
        listOf("${it.projectDir}/src/main/java", "${it.projectDir}/src/main/kotlin") 
    }))
    
    sourceDirectories.setFrom(files(subprojects.flatMap { 
        listOf("${it.projectDir}/src/main/java", "${it.projectDir}/src/main/kotlin") 
    }))
    
    classDirectories.setFrom(files(subprojects.map { 
        fileTree("${it.buildDir}/classes/kotlin/main") {
            exclude("**/R.class", "**/R\$*.class")
        }
    }))
    
    executionData.setFrom(files(subprojects.map { 
        fileTree(it.buildDir) { include("jacoco/testDebugUnitTest.exec") }
    }))
    
    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
    
    doLast {
        val coverage = executionData.files
            .filter { it.exists() }
            .map { file -> 
                java.util.Properties().apply { load(file.inputStream()) }
                    .getProperty("instruction,covered").split(",")[1].toDouble()
            }.average()
        
        if (coverage < 0.8) {
            throw GradleException("代码覆盖率低于80% (当前: ${coverage.times(100)}%)")
        }
    }
}

// SonarQube配置模板
configure<org.sonarqube.gradle.SonarExtension> {
    properties {
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.login", "admin")
        property("sonar.password", "admin")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", 
            "${layout.buildDirectory.get()}/reports/jacoco/jacocoRootReport/jacocoRootReport.xml")
    }
}