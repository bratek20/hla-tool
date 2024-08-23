package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.kotlin.kotlinFile
import com.github.bratek20.codebuilder.ops.returnBlock
import com.github.bratek20.codebuilder.ops.string
import com.github.bratek20.codebuilder.ops.variable
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.type
import com.github.bratek20.codebuilder.typescript.typeScriptFile
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.submodulePackage

class ExceptionsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Exceptions
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return modules.allExceptionNamesForCurrent().isNotEmpty()
    }

    override fun applyOperations(cb: CodeBuilder) {
        if (c.language.name() == ModuleLanguage.KOTLIN) {
            cb.kotlinFile {
                packageName = submodulePackage(SubmoduleName.Api, c)

                addImport("com.github.bratek20.architecture.exceptions.ApiException")

                modules.allExceptionNamesForCurrent().forEach {
                    addClass {
                        name = it
                        extends {
                            className = "ApiException"
                        }
                        constructor {
                            addArg {
                                name = "message"
                                type = baseType(BaseType.STRING)
                                defaultValue = "\"\""
                            }
                        }
                        addPassingArg("message")
                    }
                }
            }
        }
        if (c.language.name() == ModuleLanguage.TYPE_SCRIPT) {
            cb.typeScriptFile {
                modules.allExceptionNamesForCurrent().forEach {
                    addClass {
                        name = it
                        extends {
                            className = "ApiException"
                            generic = type(it)
                        }
                        constructor {
                            addArg {
                                name = "message"
                                type = baseType(BaseType.STRING)
                                defaultValue = "\"\""
                            }
                        }
                        addPassingArg(it)
                        addPassingArg("message")

                        addMethod {
                            name = "getTypeName"
                            returnType = baseType(BaseType.STRING)
                            body = {
                                returnBlock {
                                    string(it)
                                }
                            }
                        }
                    }
                    addFunctionCall {
                        name = "ExceptionsRegistry.register"
                        addArg {
                           variable(it)
                        }
                    }
                }
            }
        }
    }
}