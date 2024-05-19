package pl.bratek20.hla.facade.fixtures

import org.assertj.core.api.Assertions.assertThat

import pl.bratek20.hla.directory.api.*
import pl.bratek20.hla.directory.fixtures.*

import pl.bratek20.hla.facade.api.*

data class ExpectedKotlinProperties(
    var rootPackage: String? = null,
)
fun assertKotlinProperties(given: KotlinProperties, expectedInit: ExpectedKotlinProperties.() -> Unit) {
    val expected = ExpectedKotlinProperties().apply(expectedInit)

    expected.rootPackage?.let {
        assertThat(given.rootPackage).isEqualTo(it)
    }
}

data class ExpectedTypeScriptProperties(
    var srcPath: String? = null,
    var testPath: String? = null,
)
fun assertTypeScriptProperties(given: TypeScriptProperties, expectedInit: ExpectedTypeScriptProperties.() -> Unit) {
    val expected = ExpectedTypeScriptProperties().apply(expectedInit)

    expected.srcPath?.let {
        assertThat(given.srcPath).isEqualTo(it)
    }

    expected.testPath?.let {
        assertThat(given.testPath).isEqualTo(it)
    }
}

data class ExpectedHlaProperties(
    var generateWeb: Boolean? = null,
    var kotlin: (ExpectedKotlinProperties.() -> Unit)? = null,
    var typeScript: (ExpectedTypeScriptProperties.() -> Unit)? = null,
)
fun assertHlaProperties(given: HlaProperties, expectedInit: ExpectedHlaProperties.() -> Unit) {
    val expected = ExpectedHlaProperties().apply(expectedInit)

    expected.generateWeb?.let {
        assertThat(given.generateWeb).isEqualTo(it)
    }

    expected.kotlin?.let {
        assertKotlinProperties(given.kotlin, it)
    }

    expected.typeScript?.let {
        assertTypeScriptProperties(given.typeScript, it)
    }
}

data class ExpectedModuleOperationArgs(
    var moduleName: String? = null,
    var language: ModuleLanguage? = null,
    var hlaFolderPath: String? = null,
    var projectPath: String? = null,
)
fun assertModuleOperationArgs(given: ModuleOperationArgs, expectedInit: ExpectedModuleOperationArgs.() -> Unit) {
    val expected = ExpectedModuleOperationArgs().apply(expectedInit)

    expected.moduleName?.let {
        assertThat(given.moduleName.value).isEqualTo(it)
    }

    expected.language?.let {
        assertThat(given.language).isEqualTo(it)
    }

    expected.hlaFolderPath?.let {
        assertThat(pathGetValue(given.hlaFolderPath)).isEqualTo(it)
    }

    expected.projectPath?.let {
        assertThat(pathGetValue(given.projectPath)).isEqualTo(it)
    }
}