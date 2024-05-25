gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))

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

rootProject.name = "MoneyConvertor"
include(":app")
include(":core")
include(":core:ui")
include(":core:model")
include(":core:network")
include(":core:designssystem")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":core:common")
include(":core:testing")
include(":feature")
include(":feature:currency-convertor")
