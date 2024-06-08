package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.definitions.impl.HlaModules
import pl.bratek20.hla.definitions.impl.isBaseType
import pl.bratek20.hla.definitions.impl.ofBaseType
import pl.bratek20.hla.utils.pascalToCamelCase

abstract class ApiType {
    lateinit var languageTypes: LanguageTypes

    abstract fun name(): String

    open fun serializableName(): String {
        return name()
    }

    open fun deserialize(variableName: String): String {
        return variableName
    }

    open fun serialize(variableName: String): String {
        return variableName
    }

    //TODO move up
    open fun constructorCall(): String {
        return languageTypes.classConstructorCall(name())
    }
}

class BaseApiType(
    val name: BaseType
) : ApiType() {
    override fun name(): String {
        return languageTypes.mapBaseType(name)
    }
}

open class StructureApiType(
    val name: String
) : ApiType() {
    override fun name(): String {
        return name
    }
}

abstract class SimpleStructureApiType(
    val def: SimpleStructureDefinition,
    val boxedType: BaseApiType
) : StructureApiType(def.name) {

    override fun serializableName(): String {
        return boxedType.name()
    }

    override fun deserialize(variableName: String) : String {
        return constructorCall() + "($variableName)"
    }

    abstract fun unbox(variableName: String): String;

    override fun serialize(variableName: String): String {
        return unbox(variableName)
    }

    fun exampleValue(): String? {
        return def.attributes.firstOrNull { it.name == "example" }?.value
    }
}

class SimpleValueObjectApiType(
    def: SimpleStructureDefinition,
    boxedType: BaseApiType
) : SimpleStructureApiType(def, boxedType) {
    override fun constructorCall(): String {
        return languageTypes.classConstructorCall(name)
    }

    override fun unbox(variableName: String): String {
        return "$variableName.value"
    }
}

class SimpleCustomApiType(
    def: SimpleStructureDefinition,
    boxedType: BaseApiType
) : SimpleStructureApiType(def, boxedType) {
    override fun unbox(variableName: String): String {
        return languageTypes.customTypeGetterCall(name, "value") + "($variableName)"
    }

    override fun constructorCall(): String {
        return languageTypes.customTypeConstructorCall(name)
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


open class ComplexStructureApiType<T: ApiTypeField>(
    name: String,
    val fields: List<T>
) : StructureApiType(name) {

    open fun accessField(fieldName: String, variableName: String): String {
        return "$variableName.$fieldName"
    }
}

class ComplexCustomApiType(
    name: String,
    fields: List<ApiTypeField>
) : ComplexStructureApiType<ApiTypeField>(name, fields) {
    override fun serializableName(): String {
        return "Serialized$name"
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
        return "${serializableName()}.toCustomType($variableName)"
    }
}

data class SerializableTypeGetterOrSetter(
    val name: String,
    val type: ApiType,
    val field: String
)

open class SerializableApiType(
    name: String,
    fields: List<SerializableTypeApiField>
) : ComplexStructureApiType<SerializableTypeApiField>(name, fields) {
    // used by velocity
    fun getters(): List<SerializableTypeGetterOrSetter> {
        return fields.mapNotNull { it.getter() }
    }

    // used by velocity
    open fun setters(): List<SerializableTypeGetterOrSetter> {
        return emptyList()
    }

    override fun constructorCall(): String {
        return languageTypes.propertyClassConstructorCall(name())
    }
}

class ComplexValueObjectApiType(
    name: String,
    fields: List<SerializableTypeApiField>
) : SerializableApiType(name, fields)

class DataClassApiType(
    name: String,
    fields: List<SerializableTypeApiField>
) : SerializableApiType(name, fields) {

    override fun setters(): List<SerializableTypeGetterOrSetter> {
        return fields.mapNotNull { it.setter() }
    }
}


class ListApiType(
    val wrappedType: ApiType,
) : ApiType() {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }
}

class OptionalApiType(
    val wrappedType: ApiType,
) : ApiType() {
    override fun name(): String {
        return languageTypes.wrapWithOptional(wrappedType.name())
    }

    override fun serializableName(): String {
        return languageTypes.wrapWithOptional(wrappedType.serializableName())
    }

    fun unwrap(variableName: String): String {
        return languageTypes.unwrapOptional(variableName)
    }

    override fun deserialize(variableName: String): String {
        if (wrappedType is SimpleStructureApiType) {
            return languageTypes.deserializeOptionalForSimpleStructure(variableName, wrappedType.name())
        }
        return languageTypes.deserializeOptional(variableName)
    }

    override fun serialize(variableName: String): String {
        if (wrappedType is SimpleStructureApiType) {
            return languageTypes.serializeOptionalForSimpleStructure(variableName, wrappedType.name())
        }
        return languageTypes.serializeOptional(variableName)
    }
}

class EnumApiType(
    private val def: EnumDefinition,
) : ApiType() {
    override fun name(): String {
        return def.name
    }

    override fun serializableName(): String {
        return languageTypes.mapBaseType(BaseType.STRING)
    }

    fun defaultValue(): String {
        return name() + "." + def.values.first()
    }

    override fun deserialize(variableName: String): String {
        return languageTypes.deserializeEnum(name(), variableName)
    }

    override fun serialize(variableName: String): String {
        return languageTypes.serializeEnum(variableName)
    }
}

data class ApiCustomTypes(
    val simpleList: List<SimpleCustomApiType>,
    val complexList: List<ComplexCustomApiType>
)

class ApiTypeFactory(
    private val modules: HlaModules,
    private val languageTypes: LanguageTypes
) {

    fun create(type: TypeDefinition?): ApiType {
        if (type == null) {
            return createBaseApiType(BaseType.VOID)
        }

        val simpleVO = modules.findSimpleValueObject(type)
        val complexVO = modules.findComplexValueObject(type)
        val isOptional = type.wrappers.contains(TypeWrapper.OPTIONAL)
        val isList = type.wrappers.contains(TypeWrapper.LIST)
        val isBaseType = isBaseType(type.name)
        val enum = modules.findEnum(type)
        val simpleCustomType = modules.findSimpleCustomType(type)
        val complexCustomType = modules.findComplexCustomType(type)
        val dataVO = modules.findDataClass(type)

        val apiType = when {
            isOptional -> OptionalApiType(create(type.copy(wrappers = type.wrappers - TypeWrapper.OPTIONAL)))
            isList -> ListApiType(create(type.copy(wrappers = type.wrappers - TypeWrapper.LIST)))
            simpleVO != null -> SimpleValueObjectApiType(simpleVO, createBaseApiType(ofBaseType(simpleVO.typeName)))
            simpleCustomType != null -> SimpleCustomApiType(simpleCustomType, createBaseApiType(ofBaseType(simpleCustomType.typeName)))
            complexVO != null -> ComplexValueObjectApiType(type.name, createSerializableTypeFields(complexVO.fields))
            dataVO != null -> DataClassApiType(type.name, createSerializableTypeFields(dataVO.fields))
            complexCustomType != null -> ComplexCustomApiType(type.name, createComplexCustomTypeFields(type.name, complexCustomType.fields))
            isBaseType -> BaseApiType(ofBaseType(type.name))
            enum != null -> EnumApiType(enum)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        apiType.languageTypes = languageTypes

        return apiType
    }

    inline fun <reified T: SimpleStructureApiType> create(def: SimpleStructureDefinition): T {
        return create(TypeDefinition(def.name, emptyList())) as T
    }

    inline fun <reified T: ComplexStructureApiType<*>> create(def: ComplexStructureDefinition): T {
        return create(TypeDefinition(def.name, emptyList())) as T
    }

    private fun createBaseApiType(type: BaseType): BaseApiType {
        val result = BaseApiType(type)
        result.languageTypes = languageTypes
        return result
    }

    private fun createSerializableTypeFields(fields: List<FieldDefinition>): List<SerializableTypeApiField> {
        return fields.map {
            SerializableTypeApiField(it, this)
        }
    }

    private fun createComplexCustomTypeFields(className: String, fields: List<FieldDefinition>): List<ComplexCustomTypeApiField> {
        return fields.map {
            ComplexCustomTypeApiField(className, it, this)
        }
    }
}