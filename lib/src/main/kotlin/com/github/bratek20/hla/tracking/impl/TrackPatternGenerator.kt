package com.github.bratek20.hla.tracking.impl

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.apitypes.impl.ComplexStructureApiType
import com.github.bratek20.hla.attributes.getAttributeValue
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.tracking.api.TableDefinition
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.hla.typesworld.api.getField
import com.github.bratek20.utils.pascalToCamelCase

enum class TableType {
    DIMENSION,
    EVENT
}

interface TablePart {
    fun getFieldsOps(): List<FieldBuilderOps>
    fun getConstructorArgs(): List<ArgumentBuilderOps>
    fun getAssignmentOps(): List<AssignmentBuilderOps>
}

private class TrackingTypesLogic(
    private val apiTypeFactory: ApiTypeFactory,
    private val typesWorldApi: TypesWorldApi
) {
    fun getSerializationExpression(variableName: String, typeName: String): ExpressionBuilder {
        val worldType = typesWorldApi.getTypeByName(WorldTypeName(typeName))
        return if (worldType.getPath().asHla().getSubmoduleName() == SubmoduleName.Api) {
            apiTypeFactory.create(TypeDefinition(typeName, emptyList())).modernSerialize(variable(variableName))
        } else {
            variable(variableName)
        }
    }

    fun getTypeBuilder(typeName: String, serializable: Boolean): TypeBuilder {
        val worldType = typesWorldApi.getTypeByName(WorldTypeName(typeName))
        return if (worldType.getPath().asHla().getSubmoduleName() == SubmoduleName.Api) {
            val x = apiTypeFactory.create(TypeDefinition(typeName, emptyList()))
            if (serializable) x.serializableBuilder() else x.builder()
        } else {
            typeName(worldType.getName().value)
        }
    }
}

private class MyFieldsLogic(
    private val defs: List<FieldDefinition>,
    private val types: TrackingTypesLogic
): TablePart {
    override fun getFieldsOps(): List<FieldBuilderOps> {
        return defs.map {
            {
                name = it.getName()
                type = types.getTypeBuilder(it.getType().getName(), serializable = true)
            }
        }
    }

    override fun getConstructorArgs(): List<ArgumentBuilderOps> {
        return defs.map {
            {
                name = it.getName()
                type = types.getTypeBuilder(it.getType().getName(), serializable = false)
            }
        }
    }

    override fun getAssignmentOps(): List<AssignmentBuilderOps> {
        return defs.map {
            {
                left = instanceVariable(it.getName())
                right = types.getSerializationExpression(it.getName(), it.getType().getName())
            }
        }
    }
}

private class ExposedClassLogic(
    private val def: DependencyConceptDefinition,
    private val apiTypeFactory: ApiTypeFactory,
    typesWorldApi: TypesWorldApi,
    private val types: TrackingTypesLogic
): TablePart {
    private val worldClassType = typesWorldApi.getTypeByName(WorldTypeName(def.getName()))
    private val worldClass = typesWorldApi.getClassType(worldClassType)

    override fun getFieldsOps(): List<FieldBuilderOps> {
        return def.getMappedFields().map { mappedField ->
            {
                name = finalFieldName(mappedField)
                type = types.getTypeBuilder(getWorldFieldTypeName(mappedField), serializable = true)
            }
        }
    }

    override fun getConstructorArgs(): List<ArgumentBuilderOps> {
        return listOf {
            name = argVariableName()
            type = typeName(def.getName())
        }
    }

    override fun getAssignmentOps(): List<AssignmentBuilderOps> {
        return def.getMappedFields().map { mappedField ->
            val apiType = apiTypeFactory.create(TypeDefinition(def.getName(), emptyList())) as ComplexStructureApiType<*>
            val field = apiType.getField(mappedField.getName())
            return@map {
                left = instanceVariable(finalFieldName(mappedField))
                right = types.getSerializationExpression(field.access(argVariableName()), getWorldFieldTypeName(mappedField))
            }
        }
    }

    private fun argVariableName(): String {
        return pascalToCamelCase(def.getName())
    }

    private fun finalFieldName(mappedField: MappedField): String {
        return mappedField.getMappedName() ?: mappedField.getName()
    }

    private fun getWorldFieldTypeName(mappedField: MappedField): String {
        return worldClass.getField(mappedField.getName()).getType().getName().value
    }
}

class TrackingTableLogic(
    private val def: TableDefinition,
    private val type: TableType,
    private val apiTypeFactory: ApiTypeFactory,
    private val typesWorldApi: TypesWorldApi
) {
    fun getClassOps(): ClassBuilderOps {
        val types = TrackingTypesLogic(apiTypeFactory, typesWorldApi)
        val parts = def.getExposedClasses().map {
            ExposedClassLogic(it, apiTypeFactory, typesWorldApi, types)
        } + listOf(MyFieldsLogic(def.getFields(), types))

        return {
            name = def.getName()
            extends {
                className = if (type == TableType.DIMENSION) "TrackingDimension" else "TrackingEvent"
            }

            setConstructor {
                parts.flatMap { it.getConstructorArgs() }.forEach {
                    addArg(it)
                }
                setBody {
                    add(hardcodedExpression("super()").asStatement())
                    parts.flatMap { it.getAssignmentOps() }.forEach {
                        add(assignment(it))
                    }
                }
            }

            parts.flatMap { it.getFieldsOps() }.forEach {
                addField(it)
            }

            addMethod(getTableNameMethod())
        }
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

    fun populateInitSql(builder: CodeBuilder) {
        if (type == TableType.DIMENSION) {
            builder
                .line("CREATE TABLE some_dimension (")
                .line(");")
        }
        if (type == TableType.EVENT) {
            builder
                .line("CREATE TABLE some_tracking_event (")
                .line(") INHERITS (event);")
                .line("ALTER TYPE event_type ADD VALUE 'some_tracking_event';")
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
        createTableLogics(module, apiTypeFactory, typesWorldApi).forEach {
            addClass(it.getClassOps())
        }
    }
}

fun createTableLogics(module: ModuleDefinition, apiTypeFactory: ApiTypeFactory, typesWorldApi: TypesWorldApi): List<TrackingTableLogic> {
    val createTableLogic = { def: TableDefinition, type: TableType ->
        TrackingTableLogic(def, type, apiTypeFactory, typesWorldApi)
    }
    return module.getTrackingSubmodule()!!.getDimensions().map {
        createTableLogic(it, TableType.DIMENSION)
    } + module.getTrackingSubmodule()!!.getEvents().map {
        createTableLogic(it, TableType.EVENT)
    }
}

