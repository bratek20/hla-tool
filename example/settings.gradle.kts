rootProject.name = "example"

include("kotlin")

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

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from("com.github.bratek20:version-catalog:1.0.16")
        }
    }

    repositories {
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
