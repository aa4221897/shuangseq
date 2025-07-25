pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.kotlin.android") version "${Versions.kotlin}" apply false
        id("com.android.application") version "8.2.2" apply false
        id("com.google.dagger.hilt.android") version "2.51.1" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
            // 禁止其他模块声明版本目录
            strictMode = true  
        }
    }
}

rootProject.name = "MyApplication"
include(":app")
include(":core")
include(":features:analytics")
include(":features:settings")

project(":core").projectDir = file("core")
project(":features:analytics").projectDir = file("features/analytics")
project(":features:settings").projectDir = file("features/settings")