package pl.bratek20.hla.facade.fixtures

import org.assertj.core.api.Assertions.assertThat

import pl.bratek20.hla.directory.api.*
import pl.bratek20.hla.directory.fixtures.*

import pl.bratek20.hla.facade.api.*

data class ExpectedHlaProfile(
    var name: String? = null,
    var language: ModuleLanguage? = null,
    var projectPath: String? = null,
    var srcPath: String? = null,
    var fixturesPath: String? = null,
    var onlyParts: List<String>? = null,
)
fun assertHlaProfile(given: HlaProfile, expectedInit: ExpectedHlaProfile.() -> Unit) {
    val expected = ExpectedHlaProfile().apply(expectedInit)

    expected.name?.let {
        assertThat(given.getName().value).isEqualTo(it)
    }

    expected.language?.let {
        assertThat(given.language).isEqualTo(it)
    }

    expected.projectPath?.let {
        assertThat(given.projectPath).isEqualTo(it)
    }

    expected.srcPath?.let {
        assertThat(given.srcPath).isEqualTo(it)
    }

    expected.fixturesPath?.let {
        assertThat(given.fixturesPath).isEqualTo(it)
    }

    expected.onlyParts?.let {
        assertThat(given.onlyParts).hasSize(it.size)
        given.onlyParts.forEachIndexed { idx, entry -> assertThat(entry).isEqualTo(it[idx]) }
    }
}

data class ExpectedHlaProperties(
    var profiles: List<(ExpectedHlaProfile.() -> Unit)>? = null,
)
fun assertHlaProperties(given: HlaProperties, expectedInit: ExpectedHlaProperties.() -> Unit) {
    val expected = ExpectedHlaProperties().apply(expectedInit)

    expected.profiles?.let {
        assertThat(given.profiles).hasSize(it.size)
        given.profiles.forEachIndexed { idx, entry -> assertHlaProfile(entry, it[idx]) }
    }
}

data class ExpectedModuleOperationArgs(
    var hlaFolderPath: String? = null,
    var moduleName: String? = null,
    var profileName: String? = null,
)
fun assertModuleOperationArgs(given: ModuleOperationArgs, expectedInit: ExpectedModuleOperationArgs.() -> Unit) {
    val expected = ExpectedModuleOperationArgs().apply(expectedInit)

    expected.hlaFolderPath?.let {
        assertThat(pathGetValue(given.hlaFolderPath)).isEqualTo(it)
    }

    expected.moduleName?.let {
        assertThat(given.moduleName.value).isEqualTo(it)
    }

    expected.profileName?.let {
        assertThat(given.profileName.value).isEqualTo(it)
    }
}