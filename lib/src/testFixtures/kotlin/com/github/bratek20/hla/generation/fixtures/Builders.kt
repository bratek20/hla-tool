// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.generation.fixtures

import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.facade.fixtures.*
import com.github.bratek20.hla.parsing.api.*
import com.github.bratek20.hla.parsing.fixtures.*
import com.github.bratek20.utils.directory.api.*
import com.github.bratek20.utils.directory.fixtures.*

import com.github.bratek20.hla.generation.api.*

data class GeneratedPatternDef(
    var name: String = PatternName.Enums.name,
    var file: (FileDef.() -> Unit) = {},
)
fun generatedPattern(init: GeneratedPatternDef.() -> Unit = {}): GeneratedPattern {
    val def = GeneratedPatternDef().apply(init)
    return GeneratedPattern.create(
        name = PatternName.valueOf(def.name),
        file = file(def.file),
    )
}

data class GeneratedSubmoduleDef(
    var name: String = SubmoduleName.Api.name,
    var patterns: List<(GeneratedPatternDef.() -> Unit)> = emptyList(),
)
fun generatedSubmodule(init: GeneratedSubmoduleDef.() -> Unit = {}): GeneratedSubmodule {
    val def = GeneratedSubmoduleDef().apply(init)
    return GeneratedSubmodule.create(
        name = SubmoduleName.valueOf(def.name),
        patterns = def.patterns.map { it -> generatedPattern(it) },
    )
}

data class GeneratedModuleDef(
    var name: String = "someValue",
    var submodules: List<(GeneratedSubmoduleDef.() -> Unit)> = emptyList(),
)
fun generatedModule(init: GeneratedModuleDef.() -> Unit = {}): GeneratedModule {
    val def = GeneratedModuleDef().apply(init)
    return GeneratedModule.create(
        name = ModuleName(def.name),
        submodules = def.submodules.map { it -> generatedSubmodule(it) },
    )
}

data class GenerateArgsDef(
    var group: (ModuleGroupDef.() -> Unit) = {},
    var moduleToGenerate: String = "someValue",
    var onlyUpdate: Boolean = false,
)
fun generateArgs(init: GenerateArgsDef.() -> Unit = {}): GenerateArgs {
    val def = GenerateArgsDef().apply(init)
    return GenerateArgs.create(
        group = moduleGroup(def.group),
        moduleToGenerate = ModuleName(def.moduleToGenerate),
        onlyUpdate = def.onlyUpdate,
    )
}