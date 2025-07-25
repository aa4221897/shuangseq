import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

fun Project.configureJacoco() {
    with(pluginManager) {
        apply("jacoco")
    }

    extensions.configure<JacocoPluginExtension> {
        toolVersion = Versions.jacoco
    }

    tasks.withType<Test> {
        extensions.configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }

    tasks.register<JacocoReport>("jacocoTestReport") {
        dependsOn("testDebugUnitTest")
        
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
        
        classDirectories.setFrom(fileTree("${layout.buildDirectory.get()}/intermediates/javac/debug/classes") {
            exclude(
                "**/R.class",
                "**/R\$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*"
            )
        })
        
        sourceDirectories.setFrom(files("src/main/java"))
        executionData.setFrom(fileTree(layout.buildDirectory.get()) {
            include("jacoco/testDebugUnitTest.exec")
        })
    }
}