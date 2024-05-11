package pl.bratek20.hla.facade.fixtures

import org.assertj.core.api.Assertions.assertThat

import pl.bratek20.hla.directory.fixtures.*

import pl.bratek20.hla.facade.api.*

data class ExpectedGenerateModuleArgs(
    var moduleName: String? = null,
    var language: ModuleLanguage? = null,
    var hlaFolderPath: String? = null,
    var projectPath: String? = null,
)
fun assertGenerateModuleArgs(given: GenerateModuleArgs, expectedInit: ExpectedGenerateModuleArgs.() -> Unit) {
    val expected = ExpectedGenerateModuleArgs().apply(expectedInit)

    expected.moduleName?.let {
        assertThat(given.moduleName.value).isEqualTo(it)
    }

    expected.language?.let {
        assertThat(given.language).isEqualTo(it)
    }

    expected.hlaFolderPath?.let {
        assertThat(given.hlaFolderPath.value).isEqualTo(it)
    }

    expected.projectPath?.let {
        assertThat(given.projectPath.value).isEqualTo(it)
    }
}