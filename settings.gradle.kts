rootProject.name = "hla"

includeBuild("example")

pluginManagement {
    repositories {
        gradlePluginPortal()

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/bratek20/starter")
            credentials {
                username = providers.gradleProperty("githubActor").getOrElse(System.getenv("GITHUB_ACTOR"))
                password = providers.gradleProperty("githubToken").getOrElse(System.getenv("GITHUB_TOKEN"))
            }
        }
    }
}

plugins {
    id("com.github.bratek20.plugins.b20-settings") version "1.1.0"
}

b20Settings {
    catalogVersion = "1.1.6"
}

include("code-builder")
include("lib")
include("app")