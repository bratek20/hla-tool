package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.ExternalApiType
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.pascalToCamelCase

class BuildersGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Builders
    }

    data class SimpleBuilder(
        val def: SimpleStructureDefType<*>
    ) {
        // used by velocity
        fun declaration(): String {
            return "${def.funName()}(value: ${def.name()} = ${def.defaultValue()}): ${def.api.name()}"
        }

        // used by velocity
        fun body(): String {
            return "return ${def.api.constructorCall()}(value)"
        }
    }

    private fun externalTypeBuilder(type: TypeDefinition): FunctionBuilder {
        val apiType = apiTypeFactory.create(type) as ExternalApiType
        return function {
            name = pascalToCamelCase(apiType.rawName)
            addArg {
                name = "value"
                this.type = typeName(apiType.name() + "?") //TODO soft optional type wrap?
            }
            returnType = typeName(apiType.name())
            legacyBody = {
                line("return value!!") // TODO soft optional unpack?
            }
        }
    }

    override fun generateFileContent(): FileContent? {
        val defTypes = modules.allStructureDefinitions(module)
        val externalTypes = modules.allExternalTypesDefinitions(module)
        if (defTypes.areAllEmpty() && externalTypes.isEmpty()) {
            return null
        }

        val defTypeFactory = DefTypeFactory(c.language.buildersFixture())

        val simpleBuilders = (defTypes.simple).map {
            SimpleBuilder(defTypeFactory.create(apiTypeFactory.create(it)) as SimpleStructureDefType<*>)
        }
        val builders = (defTypes.complex).map {
            defTypeFactory.create(apiTypeFactory.create(it))
        }

        val externalTypesBuilders = if (externalTypes.isEmpty())
                null
            else
                CodeBuilder(lang)
                    .addMany(
                        externalTypes.map { externalTypeBuilder(it) }
                    )
                    .build()


        return contentBuilder("builders.vm")
            .put("simpleBuilders", simpleBuilders)
            .put("builders", builders)
            .put("externalTypesBuilders", externalTypesBuilders)
            .build()
    }

    override fun supportsCodeBuilder(): Boolean {
        return c.language.name() == ModuleLanguage.C_SHARP
    }

    override fun shouldGenerate(): Boolean {
        return module.getName().value == "OtherModule"
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        addClass {
            name = "OtherModuleBuilders"

            addMethod {
                static = true
                name = "buildOtherId"
                returnType = typeName("OtherId")
                addArg {
                    name = "value"
                    type = typeName("int")
                    defaultValue = "0"
                }

                setBody {
                    add(returnStatement {
                        constructorCall {
                            className = "OtherId"
                            addArg {
                                variable("value")
                            }
                        }
                    })
                }
            }

            addClass {
                name = "OtherPropertyDef"
                addField {
                    name = "id"
                    type = typeName("int")
                    setter = true
                    getter = true
                    defaultValue = const(0)
                }
                addField {
                    name = "name"
                    type = typeName("string")
                    setter = true
                    getter = true
                    defaultValue = string("someValue")
                }
            }
            addMethod {
                static = true
                name = "BuildOtherProperty"
                returnType = typeName("OtherProperty")

                setBody {
                    add(assignment {
                        left = variableDeclaration {
                            name = "def"
                        }
                        right = constructorCall {
                            className = "OtherPropertyDef"
                        }
                    })
                    //add(fun)
                    add(returnStatement {
                        methodCall {
                            target = variable("OtherProperty")
                            methodName = "create"
                            addArg {
                                functionCall {
                                    name = "buildOtherId"
                                    addArg {
                                        getterFieldAccess {
                                            objectRef = variable("def")
                                            fieldName = "id"
                                        }
                                    }
                                }
                            }
                            addArg {
                                getterFieldAccess {
                                    objectRef = variable("def")
                                    fieldName = "name"
                                }
                            }
                        }
                    })
                }
            }
        }
        addExtraEmptyLines(13)
    }
}