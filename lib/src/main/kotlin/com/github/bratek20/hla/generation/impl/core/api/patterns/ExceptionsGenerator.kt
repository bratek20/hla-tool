package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

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

    override fun getOperations(): TopLevelCodeBuilderOps = {
        if (c.language.name() == ModuleLanguage.TYPE_SCRIPT) {
            modules.allExceptionNamesForCurrent().forEach {
                addClass {
                    name = it
                    extends {
                        name = "ApiException"
                        addGeneric {
                            typeName(it)
                        }
                    }
                    setConstructor {
                        addArg {
                            name = "message"
                            type = baseType(BaseType.STRING)
                            defaultValue = emptyString()
                        }
                    }
                    addPassingArg {
                        variable(it)
                    }
                    addPassingArg {
                        variable("message")
                    }

                    addMethod {
                        name = "getTypeName"
                        returnType = baseType(BaseType.STRING)
                        legacyBody = {
                            legacyReturn {
                                legacyString(it)
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
        } else {
            modules.allExceptionNamesForCurrent().forEach {
                addClass {
                    name = it
                    extends {
                        name = "ApiException"
                    }
                    setConstructor {
                        addArg {
                            name = "message"
                            type = baseType(BaseType.STRING)
                            defaultValue = emptyString()
                        }
                    }
                    addPassingArg {
                        variable("message")
                    }
                }
            }
        }
    }

    override fun extraKotlinImports(): List<String> {
        return listOf("com.github.bratek20.architecture.exceptions.ApiException")
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf("B20.Architecture.Exceptions")
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }
}