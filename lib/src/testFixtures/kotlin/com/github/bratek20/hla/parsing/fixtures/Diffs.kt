// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.parsing.fixtures

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.definitions.fixtures.*
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.facade.fixtures.*
import com.github.bratek20.utils.directory.api.*
import com.github.bratek20.utils.directory.fixtures.*

import com.github.bratek20.hla.parsing.api.*

fun diffGroupName(given: GroupName, expected: String, path: String = ""): String {
    if (given.value != expected) { return "${path}value ${given.value} != ${expected}" }
    return ""
}

data class ExpectedModuleGroup(
    var name: String? = null,
    var modules: List<(ExpectedModuleDefinition.() -> Unit)>? = null,
    var profile: (ExpectedHlaProfile.() -> Unit)? = null,
    var dependencies: List<(ExpectedModuleGroup.() -> Unit)>? = null,
)
fun diffModuleGroup(given: ModuleGroup, expectedInit: ExpectedModuleGroup.() -> Unit, path: String = ""): String {
    val expected = ExpectedModuleGroup().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (diffGroupName(given.getName(), it) != "") { result.add(diffGroupName(given.getName(), it, "${path}name.")) }
    }

    expected.modules?.let {
        if (given.getModules().size != it.size) { result.add("${path}modules size ${given.getModules().size} != ${it.size}"); return@let }
        given.getModules().forEachIndexed { idx, entry -> if (diffModuleDefinition(entry, it[idx]) != "") { result.add(diffModuleDefinition(entry, it[idx], "${path}modules[${idx}].")) } }
    }

    expected.profile?.let {
        if (diffHlaProfile(given.getProfile(), it) != "") { result.add(diffHlaProfile(given.getProfile(), it, "${path}profile.")) }
    }

    expected.dependencies?.let {
        if (given.getDependencies().size != it.size) { result.add("${path}dependencies size ${given.getDependencies().size} != ${it.size}"); return@let }
        given.getDependencies().forEachIndexed { idx, entry -> if (diffModuleGroup(entry, it[idx]) != "") { result.add(diffModuleGroup(entry, it[idx], "${path}dependencies[${idx}].")) } }
    }

    return result.joinToString("\n")
}