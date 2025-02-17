package com.github.bratek20.hla.tracking.impl

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.attributes.getAttributeValue
import com.github.bratek20.hla.definitions.api.DependencyConceptDefinition
import com.github.bratek20.hla.definitions.api.FieldDefinition
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.queries.api.asWorldTypeName
import com.github.bratek20.hla.tracking.api.TableDefinition
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldTypeName

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

    private fun getFieldsOpsForExposedClass(exposedClassDef: DependencyConceptDefinition): List<FieldBuilderOps> {
        val exposedClassWorldType = typesWorldApi.getTypeByName(WorldTypeName(exposedClassDef.getName()))
        val exposedClass = typesWorldApi.getClassType(exposedClassWorldType)

        return exposedClassDef.getMappedFields().map { mappedField ->
            val typeName = exposedClass.getFields().first { it.getName() == mappedField.getName() }.getType().getName().value
            {
                name = mappedField.getMappedName() ?: mappedField.getName()
                type = getFieldType(typeName)
            }
        }
    }

    private fun getFieldOpsForMyField(def: FieldDefinition): FieldBuilderOps {
        return {
            name = def.getName()
            type = getFieldType(def.getType().getName())
        }
    }

    private fun getFieldType(typeName: String): TypeBuilder {
        val worldType = typesWorldApi.getTypeByName(WorldTypeName(typeName))
        return if (worldType.getPath().asHla().getSubmoduleName() == SubmoduleName.Api) {
            apiTypeFactory.create(TypeDefinition(typeName, emptyList())).serializableBuilder()
        } else {
            typeName(worldType.getName().value)
        }
    }
}

class TrackPatternGenerator: PatternGenerator() {
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