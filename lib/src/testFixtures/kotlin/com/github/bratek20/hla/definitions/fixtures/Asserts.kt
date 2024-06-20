// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.definitions.fixtures

import org.assertj.core.api.Assertions.assertThat

import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.facade.fixtures.*

import com.github.bratek20.hla.definitions.api.*

fun assertKeyDefinition(given: KeyDefinition, expectedInit: ExpectedKeyDefinition.() -> Unit) {
    val diff = diffKeyDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertEnumDefinition(given: EnumDefinition, expectedInit: ExpectedEnumDefinition.() -> Unit) {
    val diff = diffEnumDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertImplSubmoduleDefinition(given: ImplSubmoduleDefinition, expectedInit: ExpectedImplSubmoduleDefinition.() -> Unit) {
    val diff = diffImplSubmoduleDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertModuleDefinition(given: ModuleDefinition, expectedInit: ExpectedModuleDefinition.() -> Unit) {
    val diff = diffModuleDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertTypeDefinition(given: TypeDefinition, expectedInit: ExpectedTypeDefinition.() -> Unit) {
    val diff = diffTypeDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertFieldDefinition(given: FieldDefinition, expectedInit: ExpectedFieldDefinition.() -> Unit) {
    val diff = diffFieldDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertAttribute(given: Attribute, expectedInit: ExpectedAttribute.() -> Unit) {
    val diff = diffAttribute(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertSimpleStructureDefinition(given: SimpleStructureDefinition, expectedInit: ExpectedSimpleStructureDefinition.() -> Unit) {
    val diff = diffSimpleStructureDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertComplexStructureDefinition(given: ComplexStructureDefinition, expectedInit: ExpectedComplexStructureDefinition.() -> Unit) {
    val diff = diffComplexStructureDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertInterfaceDefinition(given: InterfaceDefinition, expectedInit: ExpectedInterfaceDefinition.() -> Unit) {
    val diff = diffInterfaceDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertArgumentDefinition(given: ArgumentDefinition, expectedInit: ExpectedArgumentDefinition.() -> Unit) {
    val diff = diffArgumentDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertExceptionDefinition(given: ExceptionDefinition, expectedInit: ExpectedExceptionDefinition.() -> Unit) {
    val diff = diffExceptionDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertMethodDefinition(given: MethodDefinition, expectedInit: ExpectedMethodDefinition.() -> Unit) {
    val diff = diffMethodDefinition(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}