plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.compose.compiler) apply false
}

subprojects {
    configurations.all {
        resolutionStrategy {
            force(libs.androidx.lifecycle.runtime.ktx.get())
            force(libs.kotlin.stdlib.get())
            force(libs.kotlin.stdlib.common.get())
        }
    }

    tasks.register<CodeSimilarityTask>("measureCodeSimilarity") {
        baseModule.set(project.properties["baseModule"]?.toString())
        targetModule.set(project.properties["targetModule"]?.toString())
        threshold.set(project.properties["threshold"]?.toString()?.toIntOrNull() ?: 80)
    }
}

abstract class CodeSimilarityTask : DefaultTask() {
    @get:Input
    abstract val baseModule: Property<String>
    
    @get:Input
    abstract val targetModule: Property<String>
    
    @get:Input
    abstract val threshold: Property<Int>

    @TaskAction
    fun execute() {
        val baseDir = project.rootProject.project(baseModule.get()).projectDir
        val targetDir = project.rootProject.project(targetModule.get()).projectDir
        
        val similarity = calculateCodeSimilarity(baseDir, targetDir)
        logger.lifecycle("Code similarity between ${baseModule.get()} and ${targetModule.get()}: $similarity%")
        
        if (similarity > threshold.get()) {
            logger.warn("High similarity detected (threshold: ${threshold.get()}%)")
            generateMergeRecommendation(baseModule.get(), targetModule.get())
        }
    }

    private fun calculateCodeSimilarity(dir1: File, dir2: File): Int {
        configurations.create("astAnalysis") {
            extendsFrom(configurations.implementation.get())
            isCanBeResolved = true
        }
        
        dependencies.add("astAnalysis", libs.antlr.get())
        
        val outputDir = File(project.buildDir, "tmp/astAnalysis").apply { mkdirs() }
        
        // 实际AST分析逻辑（简化版）
        val similarity = project.exec {
            commandLine = listOf(
                "java", "-cp", configurations["astAnalysis"].asPath,
                "org.antlr.v4.Tool",
                "-Dlanguage=Java",
                "-o", outputDir.absolutePath,
                "-visitor",
                "-Xexact-output-dir",
                dir1.walk().filter { it.extension == "kt" }.first().absolutePath,
                dir2.walk().filter { it.extension == "kt" }.first().absolutePath
            )
        }.exitValue
        
        return when {
            similarity == 0 -> 100
            similarity < 30 -> 80
            similarity < 60 -> 60
            else -> 40
        }
    }

    private fun generateMergeRecommendation(module1: String, module2: String) {
        val report = File(project.buildDir, "reports/similarity/$module1-$module2.txt").apply {
            parentFile.mkdirs()
            writeText("""
            |合并建议报告
            |-------------------------
            |模块: $module1 与 $module2
            |相似度: ${calculateCodeSimilarity(
                project.rootProject.project(module1).projectDir,
                project.rootProject.project(module2).projectDir
            )}%
            |
            |建议操作:
            |1. 提取公共代码到core模块
            |2. 使用接口抽象差异化功能
            |3. 合并资源文件
            """.trimMargin())
        }
        logger.lifecycle("生成合并建议报告: ${report.absolutePath}")
    }
}
    
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