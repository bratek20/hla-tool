package pl.bratek20.hla.generation.impl.core.api

import pl.bratek20.hla.definitions.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.definitions.impl.HlaModules
import pl.bratek20.hla.definitions.impl.isBaseType
import pl.bratek20.hla.definitions.impl.ofBaseType
import pl.bratek20.hla.generation.impl.core.web.dto.ComplexStructureDtoType
import pl.bratek20.hla.generation.impl.core.web.dto.DtoField
import pl.bratek20.hla.utils.camelToPascalCase
import pl.bratek20.hla.utils.pascalToCamelCase

abstract class ApiType {
    lateinit var languageTypes: LanguageTypes

    abstract fun name(): String

    open fun constructor(arg: String): String {
        return arg
    }

    open fun unboxedName(): String {
        return name()
    }

    open fun unboxedAssignment(name: String): String {
        return name
    }

    open fun unboxedType(): ApiType {
        return this
    }

    open fun constructorName(): String {
        return languageTypes.classConstructor(name())
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

    override fun unboxedType(): ApiType {
        return boxedType
    }
}

class SimpleVOApiType(
    name: String,
    boxedType: BaseApiType
) : SimpleStructureApiType(name, boxedType) {

    override fun unboxedAssignment(name: String): String {
        return "$name.value"
    }

    override fun unboxedName(): String {
        return boxedType.name()
    }

    override fun constructor(arg: String): String {
        return languageTypes.classConstructor(name) + "($arg)"
    }

    override fun constructorName(): String {
        return languageTypes.classConstructor(name)
    }
}

class SimpleCustomApiType(
    name: String,
    boxedType: BaseApiType
) : SimpleStructureApiType(name, boxedType) {

    override fun unboxedAssignment(name: String): String {
        return "XXX"
    }

    override fun unboxedName(): String {
        return boxedType.name()
    }

    override fun constructor(arg: String): String {
        return languageTypes.customTypeClassConstructor(name) + "($arg)"
    }

    override fun constructorName(): String {
        return languageTypes.customTypeClassConstructor(name)
    }

    fun createName(): String {
        return "${pascalToCamelCase(name)}Create"
    }

    fun getterName(): String {
        return "${pascalToCamelCase(name)}GetValue"
    }
}

abstract class ApiTypeField<T: ApiType>(
    val name: String,
    val type: T
) {
    abstract fun access(variableName: String): String
}

class DefaultApiTypeField(
    name: String,
    type: ApiType
): ApiTypeField<ApiType>(name, type) {
    override fun access(variableName: String): String {
        return "$variableName.$name"
    }
}

class SimpleCustomApiTypeField(
    name: String,
    type: SimpleCustomApiType,
    private val languageTypes: LanguageTypes
) : ApiTypeField<SimpleCustomApiType>(name, type) {
    override fun access(variableName: String): String {
        return languageTypes.customTypeGetterName(type.name, "value") + "($variableName)"
    }
}

class ComplexCustomApiTypeField(
    name: String,
    type: ComplexCustomApiType,
    private val languageTypes: LanguageTypes
) : ApiTypeField<ComplexCustomApiType>(name, type) {
    override fun access(variableName: String): String {
        return languageTypes.customTypeGetterName(type.name, name) + "($variableName)"
    }
}

open class ComplexStructureApiType(
    val name: String,
    val fields: List<ApiTypeField<*>>
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
    fields: List<ApiTypeField<*>>
) : ComplexStructureApiType(name, fields)

class ComplexCustomApiType(
    name: String,
    fields: List<ApiTypeField<*>>
) : ComplexStructureApiType(name, fields) {
    override fun constructor(arg: String): String {
        return languageTypes.customTypeClassConstructor(name) + "($arg)"
    }

    override fun constructorName(): String {
        return languageTypes.customTypeClassConstructor(name)
    }

    fun createName(): String {
        return "${pascalToCamelCase(name)}Create"
    }

    fun getterName(fieldName: String): String {
        return "${pascalToCamelCase(name)}Get${camelToPascalCase(fieldName)}" //TODO code duplication with language types
    }

    override fun accessField(fieldName: String, variableName: String): String {
        return getterName(fieldName) + "($variableName)"
    }
}

class PropertyApiType(
    name: String,
    fields: List<ApiTypeField<*>>
) : ComplexStructureApiType(name, fields)

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

data class ValueObjectsView(
    val simpleList: List<SimpleVOApiType>,
    val complexList: List<ComplexVOApiType>
)

data class CustomTypesView(
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
            propertyVO != null -> PropertyApiType(type.name, createFields(propertyVO.fields))
            complexCustomType != null -> ComplexCustomApiType(type.name, createFields(complexCustomType.fields))
            isBaseType -> BaseApiType(ofBaseType(type.name))
            enum != null -> EnumApiType(enum)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        apiType.languageTypes = languageTypes

        return apiType
    }

    inline fun <reified T: ComplexStructureApiType> create(def: ComplexStructureDefinition): T {
        return create(TypeDefinition(def.name, emptyList())) as T
    }

    private fun createBaseApiType(type: BaseType): BaseApiType {
        val result = BaseApiType(type)
        result.languageTypes = languageTypes
        return result
    }

    private fun createFields(fields: List<FieldDefinition>): List<ApiTypeField<*>> {
        return fields.map {
            when (val type = create(it.type)) {
                is SimpleCustomApiType -> SimpleCustomApiTypeField(it.name, type, languageTypes)
                is ComplexCustomApiType -> ComplexCustomApiTypeField(it.name, type, languageTypes)
                else -> DefaultApiTypeField(it.name, type)
            }
        }
    }
}