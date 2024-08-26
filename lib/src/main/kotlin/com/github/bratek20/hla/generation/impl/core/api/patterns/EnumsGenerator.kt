package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.codebuilder.builders.classBlock
import com.github.bratek20.codebuilder.builders.constructorCall
import com.github.bratek20.codebuilder.builders.file
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.languages.csharp.cSharpFile
import com.github.bratek20.codebuilder.languages.kotlin.kotlinFile
import com.github.bratek20.codebuilder.ops.string
import com.github.bratek20.codebuilder.languages.typescript.typeScriptFile
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.submoduleNamespace
import com.github.bratek20.hla.generation.impl.core.api.submodulePackage

class EnumsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Enums
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return module.getEnums().isNotEmpty()
    }

    override fun applyOperations(cb: CodeBuilder) {
        if (language.name() == ModuleLanguage.KOTLIN) {
            cb.kotlinFile {
                packageName = submodulePackage(SubmoduleName.Api, c)
                module.getEnums().forEach {
                    addEnum {
                        name = it.getName()
                        it.getValues().forEach { addValue(it) }
                    }
                }
            }
        }
        if (language.name() == ModuleLanguage.TYPE_SCRIPT) {
            cb.add {
                module.getEnums().forEach {
                    val enumName = it.getName()
                    classBlock {
                        name = enumName
                        extends {
                            className = "StringEnumClass"
                        }
                        it.getValues().forEach {
                            addField {
                                name = it
                                static = true
                                value = { constructorCall {
                                    className = enumName
                                    addArg {
                                        string(it)
                                    }
                                } }
                            }
                        }
                    }
                }
            }
        }
        if (language.name() == ModuleLanguage.C_SHARP) {
            cb.cSharpFile {
                namespace {
                    name = submoduleNamespace(SubmoduleName.Api, c)
                    module.getEnums().forEach {
                        addEnum {
                            name = it.getName()
                            it.getValues().forEach { addValue(it) }
                        }
                    }
                }
            }
        }
    }
}