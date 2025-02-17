package com.github.bratek20.hla.generation.impl.core.impl

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.attributes.getAttributeValue
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

        if (type == TableType.DIMENSION) {
            addField {
                name = "name"
                type = typeName("string")
            }
            addField {
                name = "amount"
                type = typeName("int")
            }
            addField {
                name = "date_range"
                type = typeName("SerializedDateRange")
            }
        }
        if (type == TableType.EVENT) {
            addField {
                name = "some_dimension_id"
                type = typeName("SomeDimension")
            }
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