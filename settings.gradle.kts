pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // Permite repositórios no projeto
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "apprural-gs"
include(":app")
