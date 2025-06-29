pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    // Removed the versionCatalogs block - Gradle automatically detects gradle/libs.versions.toml
}

rootProject.name = "multiplepages"
include(":app")