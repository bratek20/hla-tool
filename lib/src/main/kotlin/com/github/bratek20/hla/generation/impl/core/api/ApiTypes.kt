package com.github.bratek20.hla.generation.impl.core.api

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.generation.impl.core.language.LanguageTypes
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.hla.queries.api.isBaseType
import com.github.bratek20.hla.queries.api.ofBaseType
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinTypes
import com.github.bratek20.utils.pascalToCamelCase

abstract class ApiType {
    lateinit var languageTypes: LanguageTypes
    var typeModule: ModuleDefinition? = null

    fun init(languageTypes: LanguageTypes, typeModule: ModuleDefinition?) {
        this.languageTypes = languageTypes
        this.typeModule = typeModule
    }

    abstract fun name(): String

    open fun serializableName(): String {
        return name()
    }

    open fun legacyDeserialize(variableName: String): String {
        return variableName
    }

    abstract fun deserialize(variableName: String): ExpressionBuilder

    open fun legacySerialize(variableName: String): String {
        return variableName
    }

    abstract fun serialize(variableName: String): ExpressionBuilder

    override fun toString(): String {
        return "$javaClass(name=${name()})"
    }
}

class BaseApiType(
    val name: BaseType
) : ApiType() {
    override fun name(): String {
        return languageTypes.mapBaseType(name)
    }

    override fun deserialize(variableName: String): ExpressionBuilder {
        return variable(variableName)
    }

    override fun serialize(variableName: String): ExpressionBuilder {
        return variable(variableName)
    }
}

class InterfaceApiType(
    val name: String
) : ApiType() {
    override fun name(): String {
        return name
    }

    override fun deserialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }

    override fun serialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }
}

class ExternalApiType(
    val rawName: String
) : ApiType() {
    override fun name(): String {
        if (languageTypes is KotlinTypes) {
            typeModule!!.getKotlinConfig()?.let { config ->
                config.getExternalTypePackages().find { it.getName() == rawName }?.let {
                    return it.getPackageName() + "." + rawName
                }
            }
        }
        return rawName
    }

    override fun deserialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }

    override fun serialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }
}

abstract class StructureApiType(
    val name: String
) : ApiType() {
    override fun name(): String {
        return name
    }

    open fun constructorCall(): String {
        return languageTypes.classConstructorCall(name())
    }
}

abstract class SimpleStructureApiType(
    val def: SimpleStructureDefinition,
    val boxedType: BaseApiType
) : StructureApiType(def.getName()) {

    override fun serializableName(): String {
        return boxedType.name()
    }

    override fun legacyDeserialize(variableName: String) : String {
        return constructorCall() + "($variableName)"
    }

    abstract fun unbox(variableName: String): String;

    override fun legacySerialize(variableName: String): String {
        return unbox(variableName)
    }

    fun exampleValue(): String? {
        if (boxedType.name == BaseType.LONG || boxedType.name == BaseType.INT) {
            return def.getAttributes().firstOrNull { it.getName() == "example" || it.getName() == "startsFrom" }?.getValue()
        }
        return def.getAttributes().firstOrNull { it.getName() == "example" }?.getValue()
    }
}

class SimpleValueObjectApiType(
    def: SimpleStructureDefinition,
    boxedType: BaseApiType
) : SimpleStructureApiType(def, boxedType) {
    override fun constructorCall(): String {
        return languageTypes.classConstructorCall(name)
    }

    override fun deserialize(variableName: String): ExpressionBuilder {
        return constructorCall {
            className = name
            addArg {
                variable(variableName)
            }
        }
    }

    override fun serialize(variableName: String): ExpressionBuilder {
        return getterFieldAccess {
            this.variableName = variableName
            fieldName = "value"
        }
    }

    override fun unbox(variableName: String): String {
        return "$variableName.value"
    }

    fun getClassOps(): ClassBuilderOps =  {
        name = name()
        addField {
            type = typeName(boxedType.name())
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
    override fun unbox(variableName: String): String {
        return languageTypes.customTypeGetterCall(name, "value") + "($variableName)"
    }

    override fun constructorCall(): String {
        return languageTypes.customTypeConstructorCall(name)
    }

    override fun deserialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }

    override fun serialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }

    override fun legacyDeserialize(variableName: String): String {
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


open class ComplexStructureApiType<T: ComplexStructureField>(
    name: String,
    val fields: List<T>
) : StructureApiType(name) {

    open fun accessField(fieldName: String, variableName: String): String {
        return "$variableName.$fieldName"
    }

    override fun deserialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }

    override fun serialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }
}

class ComplexCustomApiType(
    name: String,
    fields: List<ComplexStructureField>
) : ComplexStructureApiType<ComplexStructureField>(name, fields) {
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

    override fun legacySerialize(variableName: String): String {
        return "${serializableName()}.fromCustomType($variableName)"
    }

    override fun legacyDeserialize(variableName: String): String {
        return "${variableName}.toCustomType()"
    }
}

data class ComplexStructureGetter(
    val name: String,
    val type: ApiType,
    val field: String
)

data class ComplexStructureSetter(
    val name: String,
    val type: ApiType,
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
}

class ComplexValueObjectApiType(
    name: String,
    fields: List<ComplexStructureField>
) : SerializableApiType(name, fields) {
    fun getClassOps(): ClassBuilderOps = {
        name = name()
        fields.forEach {
            addField {
                type = typeName(it.type.serializableName())
                name = it.name
                fromConstructor = true
            }
        }

        fields.forEach {
            addMethod {
                name = it.getterName()
                returnType = typeName(it.type.name())
                setBody {
                    add(returnStatement {
                        it.type.deserialize(it.name)
                    })
                }
            }
        }

        addMethod {
            static = true
            returnType = typeName(this@ComplexValueObjectApiType.name)
            name = "create"
            fields.forEach {
                addArg {
                    type = typeName(it.type.name())
                    name = it.name
                }
            }
            setBody {
                add(returnStatement {
                    constructorCall {
                        className = this@ComplexValueObjectApiType.name
                        fields.forEach {
                            addArg {
                                it.type.serialize(it.name)
                            }
                        }
                    }
                })
            }
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
    val wrappedType: ApiType,
) : ApiType() {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun serializableName(): String {
        return languageTypes.wrapWithList(wrappedType.serializableName())
    }

    override fun legacyDeserialize(variableName: String): String {
        if (wrappedType.name() == wrappedType.serializableName()) {
            return variableName
        }
        return languageTypes.mapListElements(variableName, "it", wrappedType.legacyDeserialize("it"))
    }

    override fun deserialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }

    override fun legacySerialize(variableName: String): String {
        if (wrappedType.name() == wrappedType.serializableName()) {
            return variableName
        }
        return languageTypes.mapListElements(variableName, "it", wrappedType.legacySerialize("it"))
    }

    override fun serialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
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

    override fun legacyDeserialize(variableName: String): String {
        val mapping = wrappedType.legacyDeserialize("it")
        val asOptional = languageTypes.deserializeOptional(variableName)
        if (mapping == "it") {
            return asOptional
        }
        return languageTypes.mapOptionalElement(asOptional, "it", mapping)
    }

    override fun deserialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }

    override fun legacySerialize(variableName: String): String {
        val mapping = wrappedType.legacySerialize("it")
        if (mapping == "it") {
            return languageTypes.serializeOptional(variableName)
        }
        return languageTypes.serializeOptional(languageTypes.mapOptionalElement(variableName, "it", mapping))
    }

    override fun serialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }
}

class EnumApiType(
    private val def: EnumDefinition,
) : ApiType() {
    override fun name(): String {
        return def.getName()
    }

    override fun serializableName(): String {
        return languageTypes.mapBaseType(BaseType.STRING)
    }

    fun defaultValue(): String {
        return name() + "." + def.getValues().first()
    }

    override fun legacyDeserialize(variableName: String): String {
        return languageTypes.deserializeEnum(name(), variableName)
    }

    override fun deserialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }

    override fun legacySerialize(variableName: String): String {
        return languageTypes.serializeEnum(variableName)
    }

    override fun serialize(variableName: String): ExpressionBuilder {
        TODO("Not yet implemented")
    }
}

data class ApiCustomTypes(
    val simpleList: List<SimpleCustomApiType>,
    val complexList: List<ComplexCustomApiType>
)

class ApiTypeFactory(
    private val modules: ModuleGroupQueries,
    private val languageTypes: LanguageTypes
) {

    fun create(type: TypeDefinition?): ApiType {
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

        val apiType = when {
            isOptional -> OptionalApiType(create(type.copy(wrappers = type.getWrappers() - TypeWrapper.OPTIONAL)))
            isList -> ListApiType(create(type.copy(wrappers = type.getWrappers() - TypeWrapper.LIST)))
            simpleVO != null -> SimpleValueObjectApiType(simpleVO, createBaseApiType(ofBaseType(simpleVO.getTypeName())))
            simpleCustomType != null -> SimpleCustomApiType(simpleCustomType, createBaseApiType(ofBaseType(simpleCustomType.getTypeName())))
            complexVO != null -> ComplexValueObjectApiType(type.getName(), createComplexStructureFields(complexVO))
            dataVO != null -> DataClassApiType(type.getName(), createComplexStructureFields(dataVO))
            complexCustomType != null -> ComplexCustomApiType(type.getName(), createComplexStructureFields(complexCustomType))
            isBaseType -> BaseApiType(ofBaseType(type.getName()))
            enum != null -> EnumApiType(enum)
            interf != null -> InterfaceApiType(type.getName())
            externalTypeName != null -> ExternalApiType(externalTypeName)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        apiType.init(languageTypes, modules.findTypeModule(type.getName()))

        if (apiType is ComplexStructureApiType<*>) {
            apiType.fields.forEach { it.init(apiType) }
        }

        return apiType
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