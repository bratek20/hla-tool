package com.github.bratek20.hla.tracking.impl

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.attributes.getAttributeValue
import com.github.bratek20.hla.definitions.api.DependencyConceptDefinition
import com.github.bratek20.hla.definitions.api.FieldDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.queries.api.asWorldTypeName
import com.github.bratek20.hla.tracking.api.TableDefinition
import com.github.bratek20.hla.typesworld.api.TypesWorldApi

private enum class TableType {
    DIMENSION,
    EVENT
}

private class TrackingTableLogic(
    private val def: TableDefinition,
    private val type: TableType,
    private val apiTypeFactory: ApiTypeFactory,
    private val typesWorldApi: TypesWorldApi
) {
    fun getClassOps(): ClassBuilderOps = {
        name = def.getName()
        extends {
            className = if (type == TableType.DIMENSION) "TrackingDimension" else "TrackingEvent"
        }

        getFieldsOps().forEach {
            addField(it)
        }

        addMethod(getTableNameMethod())
    }

    private fun getTableNameMethod(): MethodBuilderOps = {
        name = "getTableName"
        returnType = typeName("TrackingTableName")
        setBody {
            add(returnStatement {
                constructorCall {
                    className = "TrackingTableName"
                    addArg {
                        variable(getAttributeValue(def.getAttributes(), "table"))
                    }
                }
            })
        }
    }

    private fun getFieldsOps(): List<FieldBuilderOps> {
        return def.getExposedClasses().flatMap {
            getFieldsOpsForExposedClass(it)
        } + def.getFields().map {
            getFieldOpsForMyField(it)
        }
    }

    private fun getFieldsOpsForExposedClass(exposedClass: DependencyConceptDefinition): List<FieldBuilderOps> {
        if (type == TableType.DIMENSION) {
            return listOf(
                {
                    name = "name"
                    type = typeName("string")
                },
                {
                    name = "amount"
                    type = typeName("int")
                }
            )
        }
        else {
            return listOf(
            )
        }
    }

    private fun getFieldOpsForMyField(def: FieldDefinition): FieldBuilderOps {
        val worldType = typesWorldApi.getTypeByName(def.getType().asWorldTypeName())
        val fieldType = if (worldType.getPath().asHla().getSubmoduleName() == SubmoduleName.Api) {
            apiTypeFactory.create(def.getType()).serializableBuilder()
        } else {
            typeName(worldType.getName().value)
        }
        return {
            name = def.getName()
            type = fieldType
        }
    }
}

class TrackGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Track
    }

    override fun supportsCodeBuilder() = true

    override fun shouldGenerate(): Boolean {
        return c.language.name() == ModuleLanguage.TYPE_SCRIPT &&
                c.module.getTrackingSubmodule() != null
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        module.getTrackingSubmodule()!!.getDimensions().forEach {
            addClass(createTableLogic(it, TableType.DIMENSION).getClassOps())
        }
        module.getTrackingSubmodule()!!.getEvents().forEach {
            addClass(createTableLogic(it, TableType.EVENT).getClassOps())
        }
    }

    private fun createTableLogic(def: TableDefinition, type: TableType): TrackingTableLogic {
        return TrackingTableLogic(def, type, apiTypeFactory, typesWorldApi)
    }
}