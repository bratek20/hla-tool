// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.tracking.fixtures

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.definitions.fixtures.*

import com.github.bratek20.hla.tracking.api.*

data class ExpectedTableDefinition(
    var name: String? = null,
    var attributes: List<(ExpectedAttribute.() -> Unit)>? = null,
    var exposedClasses: List<(ExpectedDependencyConceptDefinition.() -> Unit)>? = null,
    var fields: List<(ExpectedFieldDefinition.() -> Unit)>? = null,
)
fun diffTableDefinition(given: TableDefinition, expectedInit: ExpectedTableDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedTableDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.attributes?.let {
        if (given.getAttributes().size != it.size) { result.add("${path}attributes size ${given.getAttributes().size} != ${it.size}"); return@let }
        given.getAttributes().forEachIndexed { idx, entry -> if (diffAttribute(entry, it[idx]) != "") { result.add(diffAttribute(entry, it[idx], "${path}attributes[${idx}].")) } }
    }

    expected.exposedClasses?.let {
        if (given.getExposedClasses().size != it.size) { result.add("${path}exposedClasses size ${given.getExposedClasses().size} != ${it.size}"); return@let }
        given.getExposedClasses().forEachIndexed { idx, entry -> if (diffDependencyConceptDefinition(entry, it[idx]) != "") { result.add(diffDependencyConceptDefinition(entry, it[idx], "${path}exposedClasses[${idx}].")) } }
    }

    expected.fields?.let {
        if (given.getFields().size != it.size) { result.add("${path}fields size ${given.getFields().size} != ${it.size}"); return@let }
        given.getFields().forEachIndexed { idx, entry -> if (diffFieldDefinition(entry, it[idx]) != "") { result.add(diffFieldDefinition(entry, it[idx], "${path}fields[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedTrackingSubmoduleDefinition(
    var dimensions: List<(ExpectedTableDefinition.() -> Unit)>? = null,
    var events: List<(ExpectedTableDefinition.() -> Unit)>? = null,
)
fun diffTrackingSubmoduleDefinition(given: TrackingSubmoduleDefinition, expectedInit: ExpectedTrackingSubmoduleDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedTrackingSubmoduleDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.dimensions?.let {
        if (given.getDimensions().size != it.size) { result.add("${path}dimensions size ${given.getDimensions().size} != ${it.size}"); return@let }
        given.getDimensions().forEachIndexed { idx, entry -> if (diffTableDefinition(entry, it[idx]) != "") { result.add(diffTableDefinition(entry, it[idx], "${path}dimensions[${idx}].")) } }
    }

    expected.events?.let {
        if (given.getEvents().size != it.size) { result.add("${path}events size ${given.getEvents().size} != ${it.size}"); return@let }
        given.getEvents().forEachIndexed { idx, entry -> if (diffTableDefinition(entry, it[idx]) != "") { result.add(diffTableDefinition(entry, it[idx], "${path}events[${idx}].")) } }
    }

    return result.joinToString("\n")
}