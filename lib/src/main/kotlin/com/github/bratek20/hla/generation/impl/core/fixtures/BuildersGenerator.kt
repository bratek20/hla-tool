package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.ExternalApiType
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.pascalToCamelCase

class SimpleBuilder(
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

class ComplexBuilder(
    val def: ComplexStructureDefType
) {
    fun getDefClassBuilder(): ClassBuilderOps = {
        name = def.defName()
        def.fields.forEach { f ->
            addField {
                name = f.name
                type = f.type.builder()
                setter = true
                getter = true
                defaultValue = variable(f.defaultValue())
            }
        }
    }

    fun getMethodBuilder(): MethodBuilderOps = {
        static = true
        name = def.funName()
        returnType = typeName(def.api.name())
        addArg {
            type = lambdaType(typeName(def.defName()))
            name = "init"
            defaultValue = emptyLambda()
        }
        setBody {
            add(assignment {
                left = variableDeclaration {
                    name = "def"
                }
                right = constructorCall {
                    className = def.defName()
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
                    target = variable(def.api.name())
                    methodName = "create"
                    def.fields.forEach { f ->
                        addArg {
                            expression(f.build("def"))
                        }
                    }
//                    addArg {
//                        methodCall {
//                            target = variable("OtherModuleBuilders")
//                            methodName = "buildOtherId"
//                            addArg {
//                                getterFieldAccess {
//                                    objectRef = variable("def")
//                                    fieldName = "id"
//                                }
//                            }
//                        }
//                    }
//                    addArg {
//                        getterFieldAccess {
//                            objectRef = variable("def")
//                            fieldName = "name"
//                        }
//                    }
                }
            })
        }
    }
}

class BuildersGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Builders
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

        val simpleBuilders = getSimpleBuilders()
        val complexBuildersDefs = getComplexBuilders().map { it.def }

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
            .put("builders", complexBuildersDefs)
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

    private fun getComplexBuilders(): List<ComplexBuilder> {
        val defTypes = modules.allStructureDefinitions(module)
        val defTypeFactory = DefTypeFactory(c.language.buildersFixture())

        return (defTypes.complex).map {
            ComplexBuilder(defTypeFactory.create(apiTypeFactory.create(it)) as ComplexStructureDefType)
        }
    }

    override fun supportsCodeBuilder(): Boolean {
        return c.language.name() == ModuleLanguage.C_SHARP
    }

    override fun shouldGenerate(): Boolean {
        return !modules.allStructureDefinitions(module).areAllEmpty()
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        addClass {
            name = moduleName + "Builders"

            getSimpleBuilders().forEach {
                addMethod(it.getMethodBuilder())
            }

            getComplexBuilders().forEach {
                addClass(it.getDefClassBuilder())
                addMethod(it.getMethodBuilder())
            }
        }
    }
}