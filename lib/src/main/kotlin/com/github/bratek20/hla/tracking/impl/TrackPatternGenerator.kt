package com.github.bratek20.hla.tracking.impl

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.apitypes.impl.ComplexStructureApiType
import com.github.bratek20.hla.apitypes.impl.OptionalApiType
import com.github.bratek20.hla.attributes.getAttributeValue
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.queries.api.asNonWrappedWorldTypeName
import com.github.bratek20.hla.queries.api.asTypeDefinition
import com.github.bratek20.hla.queries.api.asWorldTypeName
import com.github.bratek20.hla.tracking.api.TableDefinition
import com.github.bratek20.hla.typesworld.api.*
import com.github.bratek20.utils.pascalToCamelCase

enum class TableType {
    DIMENSION,
    EVENT
}

data class TrackingWorldField(val name: String, val type: WorldType, val  serializableType: WorldType, val typeDefinition: TypeDefinition)

interface TablePart {
    fun getFieldsOps(): List<FieldBuilderOps>
    fun getTrackingWorldFields(): List<TrackingWorldField>
    fun getConstructorArgs(): List<ArgumentBuilderOps>
    fun getAssignmentOps(): List<AssignmentBuilderOps>
}

private class TrackingTypesLogic(
    private val apiTypeFactory: ApiTypeFactory,
    private val typesWorldApi: TypesWorldApi
) {
    fun getSerializationExpression(variableName: String, typeDef: TypeDefinition): ExpressionBuilder {
        return apiTypeFactory.create(typeDef).modernSerialize(variable(variableName))
    }

    fun getTypeBuilder(typeDef: TypeDefinition, serializable: Boolean): TypeBuilder {
        val worldType = typesWorldApi.getTypeByName(typeDef.asWorldTypeName())
        return if (worldType.getPath().asHla().getSubmoduleName() == SubmoduleName.Api) {
            val x = apiTypeFactory.create(typeDef)
            if (serializable) x.serializableBuilder() else x.builder()
        } else {
            typeName(worldType.getName().value)
        }
    }

    fun getSerializableWorldType(typeName: String): WorldType {
        val worldType = typesWorldApi.getTypeByName(WorldTypeName(typeName))
        return if (worldType.getPath().asHla().getSubmoduleName() == SubmoduleName.Api) {
            //worldType
            apiTypeFactory.create(worldType.getName().asTypeDefinition()).serializableWorldType()
        } else {
            worldType
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
        return defs.map { def ->
            var typeToUse = def.getType()
            if(typeToUse.getWrappers().contains(TypeWrapper.OPTIONAL)) {
                val wrappers = typeToUse.getWrappers().filter { it.name != TypeWrapper.OPTIONAL.name }.map { it.name }
                typeToUse = TypeDefinition(typeToUse.getName(), wrappers)
            }
            {
                name = def.getName()
                type = types.getTypeBuilder(typeToUse, serializable = true)
            }
        }
    }

    override fun getTrackingWorldFields(): List<TrackingWorldField> {
        return defs.map { TrackingWorldField(
            name = it.getName(),
            serializableType = types.getSerializableWorldType(it.getType().getName()),
            type = types.getWorldType(it.getType().getName()),
            typeDefinition = it.getType()
        ) }
    }

    override fun getConstructorArgs(): List<ArgumentBuilderOps> {
        return defs.map {
            {
                name = it.getName()
                type = types.getTypeBuilder(it.getType(), serializable = false)
            }
        }
    }

    override fun getAssignmentOps(): List<AssignmentBuilderOps> {
        return defs.map {def ->
            {
                left = instanceVariable(def.getName())
                right = types.getSerializationExpression(def.getName(), def.getType())
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
    private val defApiType = apiTypeFactory.create(TypeDefinition(def.getName(), emptyList())) as ComplexStructureApiType<*>

    override fun getFieldsOps(): List<FieldBuilderOps> {
        return def.getMappedFields().map { mappedField ->
            {
                name = finalFieldName(mappedField)
                type = types.getTypeBuilder(getDefFieldTypeDef(mappedField), serializable = true)
            }
        }
    }

    override fun getTrackingWorldFields(): List<TrackingWorldField> {
        return def.getMappedFields().map { mappedField ->
            TrackingWorldField(
                name = finalFieldName(mappedField),
                serializableType = types.getSerializableWorldType(getWorldFieldTypeName(mappedField)),
                type = types.getWorldType(getWorldFieldTypeName(mappedField)),
                typeDefinition = getDefFieldTypeDef(mappedField)
            )
        }
    }

    private fun getDefFieldTypeDef(field: MappedField): TypeDefinition {
        return defApiType.getField(field.getName()).def.getType()
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
                right = types.getSerializationExpression(
                    field.access(argVariableName()),
                    field.def.getType()
                )
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
    fun isMultiplayerTable(): Boolean {
        return trackingTableName().endsWith("_mevent")
    }

    private val types = TrackingTypesLogic(apiTypeFactory, typesWorldApi)
    private val parts = def.getExposedClasses().map {
            ExposedClassLogic(it, apiTypeFactory, typesWorldApi, types)
        } + listOf(MyFieldsLogic(def.getFields(), types))

    fun getClassOps(): ClassBuilderOps {
        return {
            name = def.getName()
            extends {
                name = if (type == TableType.DIMENSION) "TrackingDimension" else "TrackingEvent"
            }

            setConstructor {
                parts.flatMap { tablePart -> tablePart.getConstructorArgs() }.forEach {
                    addArg(it)
                }
                setBody {
                    add(hardcodedExpression("super()").asStatement())
                    parts.flatMap { part -> part.getAssignmentOps() }.forEach {
                        add(assignment(it))
                    }
                }
            }

            parts.flatMap {part -> part.getFieldsOps() }.forEach {
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
            val id = if (isMultiplayerTable()) "mevent_id" else "event_id"
            builder.line("CONSTRAINT ${trackingTableName()}_id PRIMARY KEY ($id),")
        }

        //fields
        val worldFields = parts.flatMap { it.getTrackingWorldFields() }
        worldFields.forEachIndexed { index, field ->
            val endLineSeparator = if (index < worldFields.size - 1) "," else ""
            val nullability = if (field.typeDefinition.getWrappers().contains(TypeWrapper.OPTIONAL)) "" else " NOT NULL"

            val unwrappedFieldType = typesWorldApi.getTypeByName(field.typeDefinition.asNonWrappedWorldTypeName())
            builder.line("${field.name} ${toSqlType(unwrappedFieldType, field.serializableType)}"+ nullability + endLineSeparator)
        }

        builder.untab()

        //ending
        if (type == TableType.DIMENSION) {
            builder
                .line(");")
        }
        if (type == TableType.EVENT) {
            val eventBase = if (isMultiplayerTable()) "mevent" else "event"
            val eventTypeName = if (isMultiplayerTable()) "mevent_type" else "event_type"
            builder
                .line(") INHERITS ($eventBase);")
                .line("ALTER TYPE $eventTypeName ADD VALUE '${trackingTableName()}';")
        }
    }

    private fun toSqlType(type: WorldType, serializableType: WorldType): String {
        val hlaTypePath = type.getPath().asHla()
        val hlaSerializableTypePath = serializableType.getPath().asHla()

        if (hlaTypePath.getPatternName() == PatternName.Enums) {
            return "VARCHAR(64)"
        }
        if (hlaSerializableTypePath.getPatternName() == PatternName.Primitives) {
            return primitiveToSqlType(BaseType.valueOf(serializableType.getName().value.uppercase()))
        }
        if (hlaSerializableTypePath.getPatternName() == PatternName.Track) { // it is dimension
            return "BIGINT"
        }
        if (hlaSerializableTypePath.getSubmoduleName() == SubmoduleName.Api) {
            return "jsonb"
        }
        if(type == TrackingTypesPopulator.TRACKING_DIMENSION_WORLD_TYPE) {
            return "BIGINT"
        }
        return "???"
    }

    private fun primitiveToSqlType(type: BaseType): String {
        return when (type) {
            BaseType.STRING -> "VARCHAR(256)"
            BaseType.INT -> "INTEGER"
            BaseType.BOOL -> "BOOLEAN"
            BaseType.VOID -> TODO()
            BaseType.ANY -> TODO()
            BaseType.DOUBLE -> TODO()
            BaseType.LONG -> "BIGINT"
            BaseType.STRUCT -> TODO()
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
            if (!it.isMultiplayerTable()) {
                addClass(it.getClassOps())
            }
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

