package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.directory.api.FileContent

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
            defaultValue = def.defaultValueBuilder()
        }

        setBody {
            add(returnStatement {
                def.api.modernDeserialize(variable("value"))
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
                defaultValue = f.defaultValueBuilder()
            }
        }
    }

    fun getMethodBuilder(): MethodBuilderOps = {
        static = true
        name = def.funName()
        returnType = def.api.builder()
        addArg {
            type = lambdaType(typeName(def.defName()))
            name = "init"
            defaultValue = nullValue()
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
            add(assignment {
                left = variable("init")
                right = nullCoalescing {
                    left = variable("init")
                    defaultValue = emptyLambda(1)
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
                            f.modernBuild("def")
                        }
                    }
                }
            })
        }
    }
}

class BuildersGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Builders
    }

    override fun generateFileContent(): FileContent? {
        val defTypes = modules.allStructureDefinitions(module)
        val externalTypes = modules.allExternalTypesDefinitions(module)
        if (defTypes.areAllEmpty() && externalTypes.isEmpty()) {
            return null
        }

        val simpleBuilders = getSimpleBuilders()
        val complexBuildersDefs = getComplexBuilders().map { it.def }

        return contentBuilder("builders.vm")
            .put("simpleBuilders", simpleBuilders)
            .put("builders", complexBuildersDefs)
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