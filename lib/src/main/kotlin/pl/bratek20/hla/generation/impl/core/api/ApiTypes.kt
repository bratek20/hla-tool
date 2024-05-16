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

    open fun constructor(arg: String): String {
        return arg
    }

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

open class SimpleStructureApiType(
    val name: String,
    val boxedType: BaseApiType
) : ApiType() {
    override fun name(): String {
        return name
    }

    override fun serializableName(): String {
        return boxedType.name()
    }

    fun unboxedType(): ApiType {
        return boxedType
    }
}

class SimpleVOApiType(
    name: String,
    boxedType: BaseApiType
) : SimpleStructureApiType(name, boxedType) {

    fun unboxedAssignment(name: String): String {
        return "$name.value"
    }

    override fun constructor(arg: String): String {
        return languageTypes.classConstructorCall(name) + "($arg)"
    }

    override fun constructorCall(): String {
        return languageTypes.classConstructorCall(name)
    }
}

class SimpleCustomApiType(
    name: String,
    boxedType: BaseApiType
) : SimpleStructureApiType(name, boxedType) {

    override fun constructor(arg: String): String {
        return languageTypes.customTypeConstructorCall(name) + "($arg)"
    }

    override fun constructorCall(): String {
        return languageTypes.customTypeConstructorCall(name)
    }

    fun createName(): String {
        return "${pascalToCamelCase(name)}Create"
    }

    fun getterName(): String {
        return "${pascalToCamelCase(name)}GetValue"
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
    private val name: String,
    val fields: List<T>
) : ApiType() {
    override fun name(): String {
        return name
    }

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
    override fun constructor(arg: String): String {
        return languageTypes.customTypeConstructorCall(name()) + "($arg)"
    }

    override fun constructorCall(): String {
        return languageTypes.customTypeConstructorCall(name())
    }

    fun createName(): String {
        return "${pascalToCamelCase(name())}Create"
    }

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
    fun accessor(): String {
        return if(type is SimpleStructureApiType) "private " else ""
    }

    fun getter(): PropertyGetter? {
        if(type is SimpleStructureApiType) {
            return PropertyGetter(getterName(name), type, name)
        }
        return null
    }

    private fun getterName(fieldName: String): String {
        return "get${camelToPascalCase(fieldName)}"
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
    val simpleList: List<SimpleVOApiType>,
    val complexList: List<ComplexVOApiType>
)

data class ApiCustomTypes(
    val simpleList: List<SimpleCustomApiType>,
    val complexList: List<ComplexCustomApiType>
)

data class ArgumentView(
    val name: String,
    val type: String
)
data class MethodView(
    val name: String,
    val returnType: String?,
    val args: List<ArgumentView>,
    val throws: List<String>,
)
data class InterfaceView(
    val name: String,
    val methods: List<MethodView>
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
            simpleVO != null -> SimpleVOApiType(type.name, createBaseApiType(ofBaseType(simpleVO.typeName)))
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