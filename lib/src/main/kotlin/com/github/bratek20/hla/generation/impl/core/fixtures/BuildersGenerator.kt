package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.types.*
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

        fun getMethodBuilder(): MethodBuilderOps = {
            static = true
            name = def.funName()
            returnType = typeName(def.api.name())
            addArg {
                name = "value"
                type = typeName(def.name())
                defaultValue = variable(def.defaultValue())
            }

            setBody {
                add(returnStatement {
                    def.api.modernDeserialize("value")
                })
            }
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

        val simpleBuilders = getSimpleBuilders()
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

    private fun getSimpleBuilders(): List<SimpleBuilder> {
        val defTypes = modules.allStructureDefinitions(module)
        val defTypeFactory = DefTypeFactory(c.language.buildersFixture())

        return (defTypes.simple).map {
            SimpleBuilder(defTypeFactory.create(apiTypeFactory.create(it)) as SimpleStructureDefType<*>)
        }
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

            getSimpleBuilders().forEach {
                addMethod(it.getMethodBuilder())
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
                addArg {
                    type = lambdaType(typeName("OtherPropertyDef"))
                    name = "init"
                    defaultValue = emptyLambda()
                }
                setBody {
                    add(assignment {
                        left = variableDeclaration {
                            name = "def"
                        }
                        right = constructorCall {
                            className = "OtherPropertyDef"
                        }
                    })
                    add(lambdaCallStatement {
                        name = "init"
                        addArg {
                            variable("def")
                        }
                    })
                    add(returnStatement {
                        methodCall {
                            target = variable("OtherProperty")
                            methodName = "create"
                            addArg {
                                methodCall {
                                    target = variable("OtherModuleBuilders")
                                    methodName = "buildOtherId"
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
    }
}