pluginManagement {
    includeBuild("build-logic")
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
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "T2S"
include(":app")
include(":core:database")
include(":feature:schedule")
include(":core:designsystem")
include(":core:ui")
include(":core:model")
include(":core:common")
include(":core:network")
include(":core:domain")
include(":core:data")
include(":core:data-google")
include(":core:datastore")
