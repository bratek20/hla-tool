// DO NOT EDIT! Autogenerated by HLA tool

package pl.bratek20.hla.facade.fixtures

import org.assertj.core.api.Assertions.assertThat

import pl.bratek20.hla.directory.api.*
import pl.bratek20.hla.directory.fixtures.*

import pl.bratek20.hla.facade.api.*

fun assertModuleName(given: ModuleName, expected: String) {
    val diff = diffModuleName(given, expected)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}


fun assertProfileName(given: ProfileName, expected: String) {
    val diff = diffProfileName(given, expected)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertHlaProfile(given: HlaProfile, expectedInit: ExpectedHlaProfile.() -> Unit) {
    val diff = diffHlaProfile(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertModuleOperationArgs(given: ModuleOperationArgs, expectedInit: ExpectedModuleOperationArgs.() -> Unit) {
    val diff = diffModuleOperationArgs(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertAllModulesOperationArgs(given: AllModulesOperationArgs, expectedInit: ExpectedAllModulesOperationArgs.() -> Unit) {
    val diff = diffAllModulesOperationArgs(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}