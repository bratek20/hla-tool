// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.generation.api

import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.parsing.api.*
import com.github.bratek20.utils.directory.api.*

data class GeneratedPattern(
    private val name: String,
    private val content: String,
) {
    fun getName(): PatternName {
        return PatternName.valueOf(this.name)
    }

    fun getContent(): FileContent {
        return fileContentCreate(this.content)
    }

    companion object {
        fun create(
            name: PatternName,
            content: FileContent,
        ): GeneratedPattern {
            return GeneratedPattern(
                name = name.name,
                content = fileContentGetValue(content),
            )
        }
    }
}

data class GeneratedSubmodule(
    private val name: String,
    private val patterns: List<GeneratedPattern>,
) {
    fun getName(): SubmoduleName {
        return SubmoduleName.valueOf(this.name)
    }

    fun getPatterns(): List<GeneratedPattern> {
        return this.patterns
    }

    companion object {
        fun create(
            name: SubmoduleName,
            patterns: List<GeneratedPattern>,
        ): GeneratedSubmodule {
            return GeneratedSubmodule(
                name = name.name,
                patterns = patterns,
            )
        }
    }
}

data class GeneratedModule(
    private val name: String,
    private val submodules: List<GeneratedSubmodule>,
) {
    fun getName(): ModuleName {
        return ModuleName(this.name)
    }

    fun getSubmodules(): List<GeneratedSubmodule> {
        return this.submodules
    }

    companion object {
        fun create(
            name: ModuleName,
            submodules: List<GeneratedSubmodule>,
        ): GeneratedModule {
            return GeneratedModule(
                name = name.value,
                submodules = submodules,
            )
        }
    }
}

data class GenerateArgs(
    private val group: ModuleGroup,
    private val moduleToGenerate: String,
    private val onlyUpdate: Boolean,
) {
    fun getGroup(): ModuleGroup {
        return this.group
    }

    fun getModuleToGenerate(): ModuleName {
        return ModuleName(this.moduleToGenerate)
    }

    fun getOnlyUpdate(): Boolean {
        return this.onlyUpdate
    }

    companion object {
        fun create(
            group: ModuleGroup,
            moduleToGenerate: ModuleName,
            onlyUpdate: Boolean,
        ): GenerateArgs {
            return GenerateArgs(
                group = group,
                moduleToGenerate = moduleToGenerate.value,
                onlyUpdate = onlyUpdate,
            )
        }
    }
}