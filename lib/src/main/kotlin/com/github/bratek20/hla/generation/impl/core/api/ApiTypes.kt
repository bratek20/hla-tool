package com.github.bratek20.hla.generation.impl.core.api

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.language.LanguageTypes
import com.github.bratek20.hla.queries.api.isBaseType
import com.github.bratek20.hla.queries.api.ofBaseType
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinTypes
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.hlatypesworld.impl.B20FrontendTypesPopulator
import com.github.bratek20.hla.queries.api.BaseModuleGroupQueries
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.hla.typesworld.api.WorldTypePath
import com.github.bratek20.utils.pascalToCamelCase

interface ApiType {
    fun builder(): TypeBuilder
    fun serializableBuilder(): TypeBuilder

    fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder
    fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder
}

abstract class LegacyApiType: ApiType {
    protected val c
        get() = languageTypes.context()

    lateinit var languageTypes: LanguageTypes
    var typeModule: ModuleDefinition? = null

    fun init(languageTypes: LanguageTypes, typeModule: ModuleDefinition?) {
        this.languageTypes = languageTypes
        this.typeModule = typeModule
    }

    fun moduleName(): String {
        return typeModule?.getName()?.value ?: throw IllegalStateException("No module set for type $this")
    }

    fun asWorldType(): WorldType {
        //TODO-REF
        if (this is BaseApiType) {
            return WorldType.create(
                name = WorldTypeName(name().lowercase()),
                path = WorldTypePath("Language/Types/Api/Primitives")
            )
        }
        //TODO-FIX it should not be needed to hardcode it like that
        if (name() == "EmptyModel") {
            return B20FrontendTypesPopulator.emptyModelType
        }
        return asOptHlaType() ?: throw IllegalStateException("No HlaType for type $this")
    }

    fun asOptHlaType(): WorldType? {
        return typeModule?.let {
            WorldType.create(
                name = WorldTypeName(name()),
                path = HlaTypePath.create(
                    ModuleName(moduleName()),
                    SubmoduleName.Api,
                    PatternName.ValueObjects
                ).asWorld()
            )
        }
    }

    @Deprecated("Use builder instead", ReplaceWith("builder()"))
    open fun name(): String = builder().build(c)

    @Deprecated("Use serializableBuilder instead", ReplaceWith("serializableBuilder()"))
    open fun serializableName(): String = serializableBuilder().build(c)

    @Deprecated("Use modernDeserialize instead", ReplaceWith("modernDeserialize(variableName)"))
    open fun deserialize(variableName: String): String {
        return modernDeserialize(variable(variableName)).build(c)
    }

    @Deprecated("Use modernDeserialize instead", ReplaceWith("modernSerialize(variableName)"))
    open fun serialize(variableName: String): String {
        return modernSerialize(variable(variableName)).build(c)
    }

    override fun toString(): String {
        return "$javaClass(name=${name()})"
    }
}

class BaseApiType(
    val name: BaseType
) : LegacyApiType() {
    override fun builder(): TypeBuilder {
        val cb = codeBuilderBaseType()
        return if (cb != null) {
            baseType(cb)
        } else {
            typeName(languageTypes.mapBaseType(name))
        }
    }

    override fun serializableBuilder(): TypeBuilder {
        return builder()
    }

    private fun codeBuilderBaseType(): com.github.bratek20.codebuilder.core.BaseType? {
        return when(name) {
            BaseType.STRING -> com.github.bratek20.codebuilder.core.BaseType.STRING
            BaseType.INT -> com.github.bratek20.codebuilder.core.BaseType.INT
            BaseType.BOOL -> com.github.bratek20.codebuilder.core.BaseType.BOOL
            BaseType.VOID -> com.github.bratek20.codebuilder.core.BaseType.VOID
            BaseType.ANY -> com.github.bratek20.codebuilder.core.BaseType.ANY
            BaseType.DOUBLE -> com.github.bratek20.codebuilder.core.BaseType.DOUBLE
            BaseType.LONG -> com.github.bratek20.codebuilder.core.BaseType.LONG
            BaseType.STRUCT -> null
        }
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        return variable
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        return variable
    }
}

class InterfaceApiType(
    val name: String
) : LegacyApiType() {
    override fun builder(): TypeBuilder {
        return typeName(name)
    }

    override fun serializableBuilder(): TypeBuilder {
        TODO("Not yet implemented")
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        TODO("Not yet implemented")
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        TODO("Not yet implemented")
    }
}

class ExternalApiType(
    val rawName: String
) : LegacyApiType() {
    override fun builder(): TypeBuilder {
        return typeName(adjustedName())
    }

    override fun serializableBuilder(): TypeBuilder {
        return builder()
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        return variable
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        return variable
    }

    private fun adjustedName(): String {
        if (languageTypes is KotlinTypes) {
            typeModule!!.getKotlinConfig()?.let { config ->
                config.getExternalTypePackages().find { it.getName() == rawName }?.let {
                    return it.getPackageName() + "." + rawName
                }
            }
        }
        return rawName
    }
}

abstract class StructureApiType(
    val name: String
) : LegacyApiType() {
    open fun constructorCall(): String {
        return languageTypes.classConstructorCall(name())
    }

    override fun builder(): TypeBuilder {
        return typeName(name)
    }
}

fun extractExampleValue(attributes: List<Attribute>): String? {
    return attributes.firstOrNull { it.getName() == "example" }?.getValue()
}

fun extractExampleValueForBaseType(attributes: List<Attribute>): String? {
    return attributes.firstOrNull { it.getName() == "startsFrom" }?.getValue() ?: extractExampleValue(attributes)
}

abstract class SimpleStructureApiType(
    val def: SimpleStructureDefinition,
    val boxedType: BaseApiType
) : StructureApiType(def.getName()) {

    fun unbox(variableName: String): String {
        return serialize(variableName)
    }

    override fun serializableBuilder(): TypeBuilder {
        return boxedType.builder()
    }

    fun exampleValueBuilder(): ExpressionBuilder? {
        return extractExampleValue()?.let {
            const(it)
        }
    }

    private fun extractExampleValue(): String? {
        if (boxedType.name == BaseType.LONG || boxedType.name == BaseType.INT) {
            return extractExampleValueForBaseType(def.getAttributes())
        }
        return extractExampleValue(def.getAttributes())
    }
}

class SimpleValueObjectApiType(
    def: SimpleStructureDefinition,
    boxedType: BaseApiType
) : SimpleStructureApiType(def, boxedType) {
    override fun constructorCall(): String {
        return languageTypes.classConstructorCall(name)
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        return constructorCall {
            className = name
            addArg {
                variable
            }
        }
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        return getterFieldAccess {
            objectRef = variable
            fieldName = "value"
        }
    }

    override fun serialize(variableName: String): String {
        return "$variableName.value"
    }

    fun getClassOps(): ClassBuilderOps =  {
        name = name()
        equalsAndHashCode = true

        addField {
            type = serializableBuilder()
            name = "value"
            fromConstructor = true
            getter = true
        }
    }
}

class SimpleCustomApiType(
    def: SimpleStructureDefinition,
    boxedType: BaseApiType
) : SimpleStructureApiType(def, boxedType) {
    override fun serialize(variableName: String): String {
        return languageTypes.customTypeGetterCall(name, "value") + "($variableName)"
    }

    override fun constructorCall(): String {
        return languageTypes.customTypeConstructorCall(name)
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        return hardcodedExpression("TODO")
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        return hardcodedExpression("TODO")
    }

    override fun deserialize(variableName: String): String {
        return languageTypes.customTypeConstructorCall(name) + "($variableName)"
    }

    // used by velocity
    fun createName(): String {
        return "${pascalToCamelCase(name)}Create"    //TODO duplicated logic
    }

    // used by velocity
    fun getterName(): String {
        return "${pascalToCamelCase(name)}GetValue"    //TODO duplicated logic
    }
}


abstract class ComplexStructureApiType<T: ComplexStructureField>(
    name: String,
    val fields: List<T>
) : StructureApiType(name) {

    open fun accessField(fieldName: String, variableName: String): String {
        return "$variableName.$fieldName"
    }
}

class ComplexCustomApiType(
    name: String,
    fields: List<ComplexStructureField>
) : ComplexStructureApiType<ComplexStructureField>(name, fields) {
    override fun serializableName(): String {
        return "Serialized$name"
    }

    override fun serializableBuilder(): TypeBuilder {
        return typeName(serializableName())
    }

    override fun constructorCall(): String {
        return languageTypes.customTypeConstructorCall(name())
    }

    fun createName(): String {
        return "${pascalToCamelCase(name())}Create"
    }

    // used by velocity
    fun getterName(fieldName: String): String {
        return languageTypes.customTypeGetterName(name(), fieldName)
    }

    fun getterCall(fieldName: String): String {
        return languageTypes.customTypeGetterCall(name(), fieldName)
    }

    override fun accessField(fieldName: String, variableName: String): String {
        return getterName(fieldName) + "($variableName)"
    }

    override fun serialize(variableName: String): String {
        return "${serializableName()}.fromCustomType($variableName)"
    }

    override fun deserialize(variableName: String): String {
        return "${variableName}.toCustomType()"
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        return hardcodedExpression("TODO")
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        return hardcodedExpression("TODO")
    }
}

data class ComplexStructureGetter(
    val name: String,
    val type: LegacyApiType,
    val field: String
)

data class ComplexStructureSetter(
    val name: String,
    val type: LegacyApiType,
    val publicField: String,
    val privateField: String
)

open class SerializableApiType(
    name: String,
    fields: List<ComplexStructureField>
) : ComplexStructureApiType<ComplexStructureField>(name, fields) {
    // used by velocity
    fun getters(): List<ComplexStructureGetter> {
        return fields.mapNotNull { it.getter() }
    }

    // used by velocity
    open fun setters(): List<ComplexStructureSetter> {
        return emptyList()
    }

    override fun constructorCall(): String {
        return languageTypes.propertyClassConstructorCall(name())
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        return variable
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        return variable
    }

    override fun serializableBuilder(): TypeBuilder {
        return typeName(name())
    }
}

open class ComplexValueObjectApiType(
    name: String,
    fields: List<ComplexStructureField>
) : SerializableApiType(name, fields) {
    open fun getClassOps(): ClassBuilderOps = {
        name = name()
        fields.forEach {
            addField {
                type = it.type.serializableBuilder()
                name = it.name
                fromConstructor = true
            }
        }

        fields.forEach {
            addMethod {
                name = it.getterName()
                returnType = it.type.builder()
                setBody {
                    add(returnStatement {
                        it.type.modernDeserialize(variable(it.name))
                    })
                }
            }
        }

        addMethod {
            static = true
            returnType = this@ComplexValueObjectApiType.builder()
            name = "create"
            fields.forEach {
                addArg {
                    type = it.type.builder()
                    name = it.name
                }
            }
            setBody {
                add(returnStatement {
                    constructorCall {
                        className = this@ComplexValueObjectApiType.name
                        fields.forEach {
                            addArg {
                                it.type.modernSerialize(variable(it.name))
                            }
                        }
                    }
                })
            }
        }
    }
}

class EventApiType(
    name: String,
    fields: List<ComplexStructureField>
) : ComplexValueObjectApiType(name, fields) {
    override fun getClassOps(): ClassBuilderOps {
        val ops: ClassBuilderOps = super.getClassOps()
        return {
            this.apply(ops)
            implements = "Event"
        }
    }
}

class DataClassApiType(
    name: String,
    fields: List<ComplexStructureField>
) : SerializableApiType(name, fields) {

    override fun setters(): List<ComplexStructureSetter> {
        return fields.mapNotNull { it.setter() }
    }
}


class ListApiType(
    wrappedType: LegacyApiType,
) : WrappedApiType(wrappedType) {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun serializableName(): String {
        return languageTypes.wrapWithList(wrappedType.serializableName())
    }

    override fun builder(): TypeBuilder {
        return listType(wrappedType.builder())
    }

    override fun serializableBuilder(): TypeBuilder {
        return listType(wrappedType.serializableBuilder())
    }

    override fun deserialize(variableName: String): String {
        if (wrappedType.name() == wrappedType.serializableName()) {
            return variableName
        }
        return languageTypes.mapListElements(variableName, "it", wrappedType.deserialize("it"))
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        if (wrappedType.name() == wrappedType.serializableName()) {
            return variable
        }
        return listOp(variable).map {
            wrappedType.modernDeserialize(variable("it"))
        }
    }

    override fun serialize(variableName: String): String {
        if (wrappedType.name() == wrappedType.serializableName()) {
            return variableName
        }
        return languageTypes.mapListElements(variableName, "it", wrappedType.serialize("it"))
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        if (wrappedType.name() == wrappedType.serializableName()) {
            return variable
        }
        return listOp(variable).map {
            wrappedType.modernSerialize(variable("it"))
        }
    }
}

abstract class WrappedApiType(
    val wrappedType: LegacyApiType
): LegacyApiType()

class OptionalApiType(
    wrappedType: LegacyApiType,
) : WrappedApiType(wrappedType) {
    override fun name(): String {
        return languageTypes.wrapWithOptional(wrappedType.name())
    }

    override fun serializableName(): String {
        return languageTypes.wrapWithOptional(wrappedType.serializableName())
    }

    override fun builder(): TypeBuilder {
        return hardOptionalType(wrappedType.builder())
    }

    override fun serializableBuilder(): TypeBuilder {
        return softOptionalType(wrappedType.serializableBuilder())
    }

    fun unwrap(variableName: String): String {
        return languageTypes.unwrapOptional(variableName)
    }

    override fun deserialize(variableName: String): String {
        val mapping = wrappedType.deserialize("it")
        val asOptional = languageTypes.deserializeOptional(variableName)
        if (mapping == "it") {
            return asOptional
        }
        return languageTypes.mapOptionalElement(asOptional, "it", mapping)
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        val mapping = wrappedType.modernDeserialize(variable("it"))
        val asOptional = hardOptional(wrappedType.serializableBuilder()) {
            variable
        }

        if (mapping.build(c) == "it") {
            return asOptional
        }
        return optionalOp(asOptional).map {
            mapping
        }
    }

    override fun serialize(variableName: String): String {
        val mapping = wrappedType.serialize("it")
        if (mapping == "it") {
            return languageTypes.serializeOptional(variableName)
        }
        return languageTypes.serializeOptional(languageTypes.mapOptionalElement(variableName, "it", mapping))
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        val mapping = wrappedType.modernSerialize(variable("it"))
        if (mapping.build(c) == "it") {
            return optionalOp(variable).orElse { nullValue() }
        }

        val tmp = optionalOp(variable).map {
            mapping
        }
        return optionalOp(tmp).orElse {
            nullValue()
        }
    }
}

class EnumApiType(
    private val def: EnumDefinition,
) : LegacyApiType() {
    override fun name(): String {
        return def.getName()
    }

    override fun serializableName(): String {
        return languageTypes.mapBaseType(BaseType.STRING)
    }

    override fun builder(): TypeBuilder {
        return typeName(name())
    }

    override fun serializableBuilder(): TypeBuilder {
        return baseType(com.github.bratek20.codebuilder.core.BaseType.STRING)
    }

    fun defaultValue(): String {
        return name() + "." + def.getValues().first()
    }

    override fun deserialize(variableName: String): String {
        return languageTypes.deserializeEnum(name(), variableName)
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        val variableName = variable.build(c)
        return expression(languageTypes.deserializeEnum(name(), variableName))
    }

    override fun serialize(variableName: String): String {
        return languageTypes.serializeEnum(variableName)
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        val variableName = variable.build(c)
        return expression(languageTypes.serializeEnum(variableName))
    }
}

data class ApiCustomTypes(
    val simpleList: List<SimpleCustomApiType>,
    val complexList: List<ComplexCustomApiType>
)

class ApiTypeFactory(
    val modules: BaseModuleGroupQueries,
    val languageTypes: LanguageTypes
) {
    fun create(type: TypeDefinition?): LegacyApiType {
        if (type == null) {
            return createBaseApiType(BaseType.VOID)
        }

        val simpleVO = modules.findSimpleValueObject(type)
        val complexVO = modules.findComplexValueObject(type)
        val isOptional = type.getWrappers().contains(TypeWrapper.OPTIONAL)
        val isList = type.getWrappers().contains(TypeWrapper.LIST)
        val isBaseType = isBaseType(type.getName())
        val enum = modules.findEnum(type)
        val simpleCustomType = modules.findSimpleCustomType(type)
        val complexCustomType = modules.findComplexCustomType(type)
        val dataVO = modules.findDataClass(type)
        val interf = modules.findInterface(type)
        val externalTypeName = modules.findExternalType(type)
        val event = modules.findEvent(type)

        val apiType = when {
            isOptional -> OptionalApiType(create(withoutTypeWrapper(type, TypeWrapper.OPTIONAL)))
            isList -> ListApiType(create(withoutTypeWrapper(type, TypeWrapper.LIST)))
            simpleVO != null -> SimpleValueObjectApiType(simpleVO, createBaseApiType(ofBaseType(simpleVO.getTypeName())))
            simpleCustomType != null -> SimpleCustomApiType(simpleCustomType, createBaseApiType(ofBaseType(simpleCustomType.getTypeName())))
            complexVO != null -> ComplexValueObjectApiType(type.getName(), createComplexStructureFields(complexVO))
            dataVO != null -> DataClassApiType(type.getName(), createComplexStructureFields(dataVO))
            complexCustomType != null -> ComplexCustomApiType(type.getName(), createComplexStructureFields(complexCustomType))
            isBaseType -> BaseApiType(ofBaseType(type.getName()))
            enum != null -> EnumApiType(enum)
            interf != null -> InterfaceApiType(type.getName())
            externalTypeName != null -> ExternalApiType(externalTypeName)
            event != null -> EventApiType(type.getName(), createComplexStructureFields(event))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        apiType.init(languageTypes, modules.findTypeModule(type.getName()))

        if (apiType is ComplexStructureApiType<*>) {
            apiType.fields.forEach { it.init(apiType) }
        }

        return apiType
    }

    private fun withoutTypeWrapper(type: TypeDefinition, wrapper: TypeWrapper): TypeDefinition {
        val finalWrappers = type.getWrappers() - wrapper
        return TypeDefinition.create(
            type.getName(),
            finalWrappers
        )
    }

    inline fun <reified T: SimpleStructureApiType> create(def: SimpleStructureDefinition): T {
        return create(TypeDefinition(def.getName(), emptyList())) as T
    }

    inline fun <reified T: ComplexStructureApiType<*>> create(def: ComplexStructureDefinition): T {
        return create(TypeDefinition(def.getName(), emptyList())) as T
    }

    private fun createBaseApiType(type: BaseType): BaseApiType {
        val result = BaseApiType(type)
        result.init(languageTypes, null)
        return result
    }

    private fun createComplexStructureFields(def: ComplexStructureDefinition): List<ComplexStructureField> {
        return def.getFields().map {
            ComplexStructureField(it, this)
        }
    }
}