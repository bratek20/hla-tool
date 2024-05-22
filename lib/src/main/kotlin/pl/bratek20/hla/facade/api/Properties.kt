package pl.bratek20.hla.facade.api

import pl.bratek20.hla.directory.api.*

val PROPERTIES_KEY = pl.bratek20.architecture.properties.api.PropertyKey("properties")

data class HlaProfile(
    private val name: String,
    val language: ModuleLanguage,
    private val projectPath: String,
    private val mainPath: String,
    private val fixturesPath: String,
    val onlyParts: List<String>,
    val generateWeb: Boolean,
) {
    fun getName(): ProfileName {
        return ProfileName(this.name)
    }

    fun getProjectPath(): Path {
        return pathCreate(this.projectPath)
    }

    fun getMainPath(): Path {
        return pathCreate(this.mainPath)
    }

    fun getFixturesPath(): Path {
        return pathCreate(this.fixturesPath)
    }
}
data class HlaProperties(
    val profiles: List<HlaProfile>,
) {
}