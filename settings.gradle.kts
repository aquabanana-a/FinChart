pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // SciChart
        maven { url = uri("https://www.myget.org/F/abtsoftware-bleeding-edge/maven") }
        maven { url = uri("https://www.myget.org/F/abtsoftware/maven") }
    }
}

rootProject.name = "FinChart"
include(":app")
include(":app_data")
include(":app_model")
