plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("application")
    id("maven-publish")
}

dependencies {
    implementation(project(":lib"))

    implementation(libs.bratek20.architecture)
    implementation(libs.bratek20.logs.logback)
}

application {
    mainClass.set("com.github.bratek20.hla.app.Main")
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "com.github.bratek20.hla.app.Main")
    }
}

val toolVersion = "1.0.0"

//publishing {
//    publications {
//        create<MavenPublication>("gpr") {
//            artifact(tasks.shadowJar.get())
//
//            groupId = "com.github.bratek20.hla"
//            artifactId = "hla-tool"
//            version = toolVersion
//        }
//    }
//
//    repositories {
//        val githubActor: String? = if (extra.has("githubActor")) extra["githubActor"] as String else System.getenv("GITHUB_ACTOR")
//        val githubToken: String? = if (extra.has("githubToken")) extra["githubToken"] as String else System.getenv("GITHUB_TOKEN")
//
//        if (githubActor != null && githubToken != null) {
//            println("GitHub credentials found. Configuring GitHub Packages publication.")
//            maven {
//                name = "GitHubPackages"
//                url = uri("https://maven.pkg.github.com/bratek20/hla-tool")
//                credentials {
//                    username = githubActor
//                    password = githubToken
//                }
//            }
//        } else {
//            println("GitHub credentials not found. Skipping GitHub Packages configuration.")
//        }
//    }
//}