package com.github.bratek20.hla.generation.impl.core.impl

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.attributes.getAttributeValue
import com.github.bratek20.hla.definitions.api.DependencyConceptDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.tracking.api.TableDefinition

private enum class TableType {
    DIMENSION,
    EVENT
}

private class TrackingTableLogic(
    private val def: TableDefinition,
    private val type: TableType
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
        } + getFieldsOpsForMyFields()
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

    private fun getFieldsOpsForMyFields(): List<FieldBuilderOps> {
        if (type == TableType.DIMENSION) {
            return listOf(
                {
                    name = "date_range"
                    type = typeName("SerializedDateRange")
                }
            )
        } else {
            return listOf(
                {
                    name = "some_dimension_id"
                    type = typeName("SomeDimension")
                }
            )
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
            addClass(TrackingTableLogic(it, TableType.DIMENSION).getClassOps())
        }
        module.getTrackingSubmodule()!!.getEvents().forEach {
            addClass(TrackingTableLogic(it, TableType.EVENT).getClassOps())
        }
    }
}