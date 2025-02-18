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
import com.github.bratek20.hla.typesworld.api.*
import com.github.bratek20.utils.pascalToCamelCase
import com.github.bratek20.utils.stringify

enum class TableType {
    DIMENSION,
    EVENT
}

interface TablePart {
    fun getFieldsOps(): List<FieldBuilderOps>
    fun getWorldFields(): List<WorldClassField>
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
        val worldType = getWorldType(typeName)
        return if (worldType.getPath().asHla().getSubmoduleName() == SubmoduleName.Api) {
            val x = apiTypeFactory.create(TypeDefinition(typeName, emptyList()))
            if (serializable) x.serializableBuilder() else x.builder()
        } else {
            typeName(worldType.getName().value)
        }
    }

    fun getWorldType(typeName: String): WorldType {
        return typesWorldApi.getTypeByName(WorldTypeName(typeName))
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

    override fun getWorldFields(): List<WorldClassField> {
        return defs.map { WorldClassField.create(
            name = it.getName(),
            type = types.getWorldType(it.getType().getName())
        ) }
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

    override fun getWorldFields(): List<WorldClassField> {
        return def.getMappedFields().map { mappedField ->
            WorldClassField.create(
                name = finalFieldName(mappedField),
                type = types.getWorldType(getWorldFieldTypeName(mappedField))
            )
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
    private val types = TrackingTypesLogic(apiTypeFactory, typesWorldApi)
    private val parts = def.getExposedClasses().map {
            ExposedClassLogic(it, apiTypeFactory, typesWorldApi, types)
        } + listOf(MyFieldsLogic(def.getFields(), types))

    fun getClassOps(): ClassBuilderOps {
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
                        string(trackingTableName())
                    }
                }
            })
        }
    }

    private fun trackingTableName() = getAttributeValue(def.getAttributes(), "table").replace("\"", "")

    fun populateInitSql(builder: CodeBuilder) {
        builder.line("CREATE TABLE ${trackingTableName()} (")

        builder.tab()

        //primary key
        if (type == TableType.DIMENSION) {
            builder.line("${trackingTableName()}_id BIGINT DEFAULT NEXTVAL('common.the_sequence'::regclass) CONSTRAINT ${trackingTableName()}_id PRIMARY KEY,")
        }
        if (type == TableType.EVENT)  {
            builder.line("CONSTRAINT ${trackingTableName()}_id PRIMARY KEY (event_id),")
        }

        //fields
        val worldFields = parts.flatMap { it.getWorldFields() }
        worldFields.forEachIndexed { index, field ->
            val endLineSeparator = if (index < worldFields.size - 1) "," else ""
            builder.line("${field.getName()} ${toSqlType(field.getType())} NOT NULL" + endLineSeparator)
        }

        builder.untab()

        //ending
        if (type == TableType.DIMENSION) {
            builder
                .line(");")
        }
        if (type == TableType.EVENT) {
            builder
                .line(") INHERITS (event);")
                .line("ALTER TYPE event_type ADD VALUE '${trackingTableName()}';")
        }
    }

    private fun toSqlType(type: WorldType): String {
        return "VARCHAR(256)"
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

