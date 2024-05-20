package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.definitions.impl.HlaModules
import pl.bratek20.hla.definitions.impl.isBaseType
import pl.bratek20.hla.definitions.impl.ofBaseType
import pl.bratek20.hla.utils.camelToPascalCase
import pl.bratek20.hla.utils.pascalToCamelCase

abstract class ApiType {
    lateinit var languageTypes: LanguageTypes

    abstract fun name(): String

    open fun serializableName(): String {
        return name()
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
    name: String,
    val boxedType: BaseApiType
) : StructureApiType(name) {

    override fun serializableName(): String {
        return boxedType.name()
    }

    fun deserialize(variableName: String) : String {
        return constructorCall() + "($variableName)"
    }

    abstract fun unbox(variableName: String): String;
}

class NamedApiType(
    name: String,
    boxedType: BaseApiType
) : SimpleStructureApiType(name, boxedType) {
    override fun constructorCall(): String {
        return languageTypes.classConstructorCall(name)
    }

    override fun unbox(variableName: String): String {
        return "$variableName.value"
    }
}

class SimpleCustomApiType(
    name: String,
    boxedType: BaseApiType
) : SimpleStructureApiType(name, boxedType) {
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

open class ApiTypeField(
    val name: String,
    val type: ApiType
) {
    open fun access(variableName: String): String {
        return "$variableName.$name"
    }
}

class ComplexCustomApiTypeField(
    private val className: String,
    name: String,
    type: ApiType,
    private val languageTypes: LanguageTypes
) : ApiTypeField(name, type) {
    override fun access(variableName: String): String {
        return languageTypes.customTypeGetterCall(className, name) + "($variableName)"
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

class ComplexVOApiType(
    name: String,
    fields: List<ApiTypeField>
) : ComplexStructureApiType<ApiTypeField>(name, fields)

class ComplexCustomApiType(
    name: String,
    fields: List<ApiTypeField>
) : ComplexStructureApiType<ApiTypeField>(name, fields) {
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
}

data class PropertyGetter(
    val name: String,
    val type: ApiType,
    val field: String
)
class PropertyApiTypeField(
    name: String,
    type: ApiType,
): ApiTypeField(name, type) {
    override fun access(variableName: String): String {
        if (type is SimpleStructureApiType) {
            return "$variableName.${getterName()}()"
        }
        return "$variableName.$name"
    }

    fun accessor(): String {
        return if(type is SimpleStructureApiType) "private " else ""
    }

    fun getter(): PropertyGetter? {
        if(type is SimpleStructureApiType) {
            return PropertyGetter(getterName(), type, name)
        }
        return null
    }

    private fun getterName(): String {
        return "get${camelToPascalCase(name)}"
    }
}

class PropertyApiType(
    name: String,
    fields: List<PropertyApiTypeField>
) : ComplexStructureApiType<PropertyApiTypeField>(name, fields) {
    // used by velocity
    fun getters(): List<PropertyGetter> {
        return fields.mapNotNull { it.getter() }
    }

    override fun constructorCall(): String {
        return languageTypes.propertyClassConstructor(name())
    }
}

class ListApiType(
    val wrappedType: ApiType,
) : ApiType() {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }
}

class EnumApiType(
    private val def: EnumDefinition,
) : ApiType() {
    override fun name(): String {
        return def.name
    }

    fun defaultValue(): String {
        return name() + "." + def.values.first()
    }
}

data class ApiValueObjects(
    val simpleList: List<NamedApiType>,
    val complexList: List<ComplexVOApiType>
)

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

        val simpleVO = modules.findSimpleVO(type)
        val complexVO = modules.findComplexVO(type)
        val isList = type.wrappers.contains(TypeWrapper.LIST)
        val isBaseType = isBaseType(type.name)
        val enum = modules.findEnum(type)
        val propertyVO = modules.findPropertyVO(type)
        val simpleCustomType = modules.findSimpleCustomType(type)
        val complexCustomType = modules.findComplexCustomType(type)

        val apiType = when {
            isList -> ListApiType(create(type.copy(wrappers = type.wrappers - TypeWrapper.LIST)))
            simpleVO != null -> NamedApiType(type.name, createBaseApiType(ofBaseType(simpleVO.typeName)))
            simpleCustomType != null -> SimpleCustomApiType(type.name, createBaseApiType(ofBaseType(simpleCustomType.typeName)))
            complexVO != null -> ComplexVOApiType(type.name, createFields(complexVO.fields))
            propertyVO != null -> PropertyApiType(type.name, createPropertyFields(propertyVO.fields))
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

    private fun createFields(fields: List<FieldDefinition>): List<ApiTypeField> {
        return fields.map {
            ApiTypeField(it.name, create(it.type))
        }
    }

    private fun createPropertyFields(fields: List<FieldDefinition>): List<PropertyApiTypeField> {
        return fields.map {
            PropertyApiTypeField(it.name, create(it.type))
        }
    }

    private fun createComplexCustomTypeFields(className: String, fields: List<FieldDefinition>): List<ComplexCustomApiTypeField> {
        return fields.map {
            ComplexCustomApiTypeField(className, it.name, create(it.type), languageTypes)
        }
    }
}