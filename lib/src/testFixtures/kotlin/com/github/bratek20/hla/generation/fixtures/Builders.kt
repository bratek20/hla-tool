// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.generation.fixtures

import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.definitions.fixtures.*
import com.github.bratek20.hla.directory.api.*
import com.github.bratek20.hla.directory.fixtures.*
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.facade.fixtures.*

import com.github.bratek20.hla.generation.api.*

data class GenerateArgsDef(
    var moduleName: String = "someValue",
    var modules: List<(ModuleDefinitionDef.() -> Unit)> = emptyList(),
    var onlyUpdate: Boolean = false,
    var profile: (HlaProfileDef.() -> Unit) = {},
)
fun generateArgs(init: GenerateArgsDef.() -> Unit = {}): GenerateArgs {
    val def = GenerateArgsDef().apply(init)
    return GenerateArgs.create(
        moduleName = ModuleName(def.moduleName),
        modules = def.modules.map { it -> moduleDefinition(it) },
        onlyUpdate = def.onlyUpdate,
        profile = hlaProfile(def.profile),
    )
}

data class GenerateResultDef(
    var main: (DirectoryDef.() -> Unit) = {},
    var fixtures: (DirectoryDef.() -> Unit) = {},
    var tests: (DirectoryDef.() -> Unit)? = null,
)
fun generateResult(init: GenerateResultDef.() -> Unit = {}): GenerateResult {
    val def = GenerateResultDef().apply(init)
    return GenerateResult.create(
        main = directory(def.main),
        fixtures = directory(def.fixtures),
        tests = def.tests?.let { it -> directory(it) },
    )
}