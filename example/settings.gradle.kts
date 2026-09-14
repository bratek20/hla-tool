rootProject.name = "example"

include("kotlin")
include("kotlin2")
include("tests")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()

        val githubActor: String? = if (extra.has("githubActor")) extra["githubActor"] as String else System.getenv("GITHUB_ACTOR")
        val githubToken: String? = if (extra.has("githubToken")) extra["githubToken"] as String else System.getenv("GITHUB_TOKEN")

        if (githubActor != null && githubToken != null) {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/bratek20/starter")
                credentials {
                    username = githubActor
                    password = githubToken
                }
            }
        }
    }
}

plugins {
    id("com.github.bratek20.plugins.b20-settings") version "2.0.0"
}

b20Settings {
    catalogVersion = "2.0.0"
}
