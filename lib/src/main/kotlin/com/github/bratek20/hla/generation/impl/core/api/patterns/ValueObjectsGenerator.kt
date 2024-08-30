package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.builders.constructorCall
import com.github.bratek20.codebuilder.builders.returnStatement
import com.github.bratek20.codebuilder.builders.variable
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.ComplexValueObjectApiType
import com.github.bratek20.hla.generation.impl.core.api.SimpleValueObjectApiType
import com.github.bratek20.utils.directory.api.FileContent

class ValueObjectsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ValueObjects
    }

    override fun supportsCodeBuilder(): Boolean {
        return c.language.name() == ModuleLanguage.C_SHARP
    }

    override fun shouldGenerate(): Boolean {
        return module.getSimpleValueObjects().isNotEmpty() || module.getComplexValueObjects().isNotEmpty()
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        addClass {
            name = "OtherId"
            addField {
                type = typeName("int")
                name = "value"
                fromConstructor = true
                getter = true
            }
        }
        addClass {
            name = "OtherProperty"
            addField {
                type = typeName("int")
                name = "id"
                fromConstructor = true
            }
            addField {
                type = typeName("string")
                name = "name"
                fromConstructor = true
            }

            addMethod {
                name = "GetId"
                returnType = typeName("OtherId")
                setBody {
                    add(returnStatement {
                        constructorCall {
                            className = "OtherId"
                            addArg {
                                variable("id")
                            }
                        }
                    })
                }
            }
        }
        addExtraEmptyLines(31)
    }

    override fun generateFileContent(): FileContent? {
        val simpleValueObjects = module.getSimpleValueObjects().map { apiTypeFactory.create<SimpleValueObjectApiType>(it) }
        val complexValueObjects = module.getComplexValueObjects().map { apiTypeFactory.create<ComplexValueObjectApiType>(it) }

        if (simpleValueObjects.isEmpty() && complexValueObjects.isEmpty()) {
            return null
        }

        return contentBuilder("valueObjects.vm")
            .put("simpleValueObjects", simpleValueObjects)
            .put("complexValueObjects", complexValueObjects)
            .build()
    }


}