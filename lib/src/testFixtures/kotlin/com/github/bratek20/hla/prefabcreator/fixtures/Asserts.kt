// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.prefabcreator.fixtures

import org.assertj.core.api.Assertions.assertThat

import com.github.bratek20.hla.prefabcreator.api.*

fun assertPrefabChildBlueprint(given: PrefabChildBlueprint, expectedInit: ExpectedPrefabChildBlueprint.() -> Unit) {
    val diff = diffPrefabChildBlueprint(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}

fun assertPrefabBlueprint(given: PrefabBlueprint, expectedInit: ExpectedPrefabBlueprint.() -> Unit) {
    val diff = diffPrefabBlueprint(given, expectedInit)
    assertThat(diff).withFailMessage(diff).isEqualTo("")
}