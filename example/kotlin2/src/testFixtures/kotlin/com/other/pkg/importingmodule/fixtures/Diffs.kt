// DO NOT EDIT! Autogenerated by HLA tool

package com.other.pkg.importingmodule.fixtures

import com.some.pkg.othermodule.api.*
import com.some.pkg.othermodule.fixtures.*

import com.other.pkg.importingmodule.api.*

fun diffImportingEnum(given: ImportingEnum, expected: String, path: String = ""): String {
    if (given != ImportingEnum.valueOf(expected)) { return "${path}value ${given.name} != ${expected}" }
    return ""
}

data class ExpectedImportingProperty(
    var other: (ExpectedOtherProperty.() -> Unit)? = null,
)
fun diffImportingProperty(given: ImportingProperty, expectedInit: ExpectedImportingProperty.() -> Unit, path: String = ""): String {
    val expected = ExpectedImportingProperty().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.other?.let {
        if (diffOtherProperty(given.getOther(), it) != "") { result.add(diffOtherProperty(given.getOther(), it, "${path}other.")) }
    }

    return result.joinToString("\n")
}