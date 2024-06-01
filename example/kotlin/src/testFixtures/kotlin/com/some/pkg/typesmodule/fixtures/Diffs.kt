// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.typesmodule.fixtures

import com.some.pkg.typesmodule.api.*

fun diffDate(given: Date, expected: String, path: String = ""): String {
    if (given.value != expected) { return "${path}value ${given.value} != $expected" }
    return ""
}

data class ExpectedDateRange(
    var from: String? = null,
    var to: String? = null,
)
fun diffDateRange(given: DateRange, expectedInit: ExpectedDateRange.() -> Unit, path: String = ""): String {
    val expected = ExpectedDateRange().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.from?.let {
        if (diffDate(dateRangeGetFrom(given), it) != "") { result.add(diffDate(dateRangeGetFrom(given), it, "${path}from.")) }
    }

    expected.to?.let {
        if (diffDate(dateRangeGetTo(given), it) != "") { result.add(diffDate(dateRangeGetTo(given), it, "${path}to.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedDateRangeProperty(
    var from: String? = null,
    var to: String? = null,
)
fun diffDateRangeProperty(given: DateRangeProperty, expectedInit: ExpectedDateRangeProperty.() -> Unit, path: String = ""): String {
    val expected = ExpectedDateRangeProperty().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.from?.let {
        if (diffDate(given.getFrom(), it) != "") { result.add(diffDate(given.getFrom(), it, "${path}from.")) }
    }

    expected.to?.let {
        if (diffDate(given.getTo(), it) != "") { result.add(diffDate(given.getTo(), it, "${path}to.")) }
    }

    return result.joinToString("\n")
}