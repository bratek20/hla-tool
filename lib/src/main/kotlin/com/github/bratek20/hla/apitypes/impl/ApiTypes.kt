package com.github.bratek20.hla.apitypes.impl

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.apitypes.api.ApiType
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.generation.impl.core.api.ComplexStructureField
import com.github.bratek20.hla.generation.impl.core.language.LanguageTypes
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinTypes
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.impl.B20FrontendTypesPopulator
import com.github.bratek20.hla.hlatypesworld.impl.PrimitiveTypesPopulator
import com.github.bratek20.hla.importscalculation.impl.mapToImport
import com.github.bratek20.hla.queries.api.asTypeDefinition
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.utils.destringify
import com.github.bratek20.utils.pascalToCamelCase

abstract class ApiTypeLogic: ApiType {
    protected val c
        get() = languageTypes.context()

    protected val language
        get() = c.lang

    lateinit var languageTypes: LanguageTypes
    var typeModule: ModuleDefinition? = null
    var worldType: WorldType? = null

    fun init(languageTypes: LanguageTypes, typeModule: ModuleDefinition?, worldType: WorldType?) {
        this.languageTypes = languageTypes
        this.typeModule = typeModule
        this.worldType = worldType
    }

    protected fun moduleName(): String {
        return typeModule?.getName()?.value ?: throw IllegalStateException("No module set for type $this")
    }

    override fun asWorldType(): WorldType {
        //TODO-FIX it should not be needed to hardcode it like that
        if (name() == "EmptyModel") {
            return B20FrontendTypesPopulator.emptyModelType
        }
        return worldType ?: throw IllegalStateException("No world type set for type $this")
    }

    override fun serializableWorldType(): WorldType {
        return asWorldType()
    }

    @Deprecated("Use builder instead", ReplaceWith("builder()"))
    fun name(): String = builder().build(c)

    @Deprecated("Use serializableBuilder instead", ReplaceWith("serializableBuilder()"))
    fun serializableName(): String = serializableBuilder().build(c)

    @Deprecated("Use modernDeserialize instead", ReplaceWith("modernDeserialize(variableName)"))
    fun deserialize(variableName: String): String {
        return modernDeserialize(variable(variableName)).build(c)
    }

    @Deprecated("Use modernDeserialize instead", ReplaceWith("modernSerialize(variableName)"))
    fun serialize(variableName: String): String {
        return modernSerialize(variable(variableName)).build(c)
    }

    override fun toString(): String {
        return "$javaClass(name=${name()})"
    }
}

class BaseApiType(
    val name: BaseType
) : ApiTypeLogic() {
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

    override fun getExample(): Any {
        return when(name) {
            BaseType.STRING -> "someValue"
            BaseType.INT -> 0
            BaseType.BOOL -> false
            BaseType.VOID ->{}
            BaseType.ANY -> "This is any type - it can contain anything"
            BaseType.DOUBLE -> 0.0
            BaseType.LONG -> 0
            BaseType.STRUCT -> {}
        }
    }

    companion object {
        fun isVoid(type: ApiType): Boolean {
            return type is BaseApiType && type.name == BaseType.VOID
        }

        fun isAny(type: ApiType): Boolean {
            return type is BaseApiType && type.name == BaseType.ANY
        }
    }
}

class InterfaceApiType(
    val name: String
) : ApiTypeLogic() {
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

    override fun getExample(): Any {
        TODO("Not yet implemented")
    }
}

class ExternalApiType(
    val rawName: String
) : ApiTypeLogic() {
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

    override fun getExample(): Any {
        return "This is an external type, no example available"
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

class WorldApiType(
    val type: WorldType
) : ApiTypeLogic() {
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

    override fun getExample(): Any {
        TODO("Not yet implemented")
    }

    private fun adjustedName(): String {
        if (languageTypes is KotlinTypes) {
            return mapToImport(type.getPath()).lowercase() + "." + type.getName().value
        }
        return type.getName().value
    }
}

abstract class StructureApiType(
    val name: String
) : ApiTypeLogic() {
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

fun extractExampleValueForNumericType(attributes: List<Attribute>): String? {
    return attributes.firstOrNull { it.getName() == "startsFrom" }?.getValue() ?: extractExampleValue(attributes)
}

abstract class SimpleStructureApiType(
    val def: SimpleStructureDefinition,
    val boxedType: BaseApiType
) : StructureApiType(def.getName()) {
    override fun serializableWorldType(): WorldType {
        return boxedType.asWorldType()
    }

    fun unbox(variableName: String): String {
        return serialize(variableName)
    }

    override fun serializableBuilder(): TypeBuilder {
        return boxedType.builder()
    }

    fun exampleValueBuilder(): ExpressionBuilder? {
        return extractExampleValueFromAttributes()?.let {
            const(it)
        }
    }

    private fun extractExampleValueFromAttributes(): String? {
        if (boxedType.name == BaseType.LONG || boxedType.name == BaseType.INT) {
            return extractExampleValueForNumericType(def.getAttributes())
        }
        return extractExampleValue(def.getAttributes())
    }

    override fun getExample(): Any {
        val exampleValueFromAttributes = extractExampleValueFromAttributes()?.let { destringify(it) }
        if (exampleValueFromAttributes != null) {
            return exampleValueFromAttributes
        }
        if (boxedType.name == BaseType.STRING) {
            return pascalToCamelCase(def.getName())
        }
        return boxedType.getExample()
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
        if (language is TypeScript) {
            return methodCall {
                target = variable
                methodName = "getValue"
            }
        }

        return getterFieldAccess {
            objectRef = variable
            fieldName = "value"
        }
    }

    fun getClassOps(): ClassBuilderOps =  {
        name = this@SimpleValueObjectApiType.name
        if (c.lang is CSharp) {
            extends {
                name = "ValueObject"
            }

            addMethod {
                overridesClassMethod = true
                returnType = baseType(com.github.bratek20.codebuilder.core.BaseType.STRING)
                name = "toString"
                setBody {
                    add(returnStatement {
                        methodCall {
                            target = getterField("value")
                            methodName = "toString"
                        }
                    })
                }
            }
        }

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

    override fun constructorCall(): String {
        return languageTypes.customTypeConstructorCall(name)
    }
    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        return hardcodedExpression(languageTypes.customTypeGetterCall(name, "value") + "(${variable.build(c)})")
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        return hardcodedExpression(languageTypes.customTypeConstructorCall(name) + "(${variable.build(c)})")
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

    fun getField(fieldName: String): T {
        return fields.first { it.name == fieldName }
    }

    override fun getExample(): Any {
        val fieldsMap: MutableMap<String, Any?> = mutableMapOf()
        fields.map { field ->
            val builderValue = field.exampleValueBuilder()?.build(c)?.let {
                destringify(it)
            }
            val fieldTypeDef = field.def.getType()
            val fieldName = fieldTypeDef.getName()
            if(fieldName == name) {
                if(field.type is ListApiType) {
                    fieldsMap[field.privateName()] = emptyList<Any>()
                }else if (field.type is OptionalApiType){
                    fieldsMap[field.privateName()] = null
                }else {
                    error("Field type is $fieldName and is recursive, but it is not wrapped in Optional or List")
                }
            }else {
                val finalValue = builderValue ?: field.type.getExample()
                fieldsMap[field.privateName()] = finalValue
            }
        }
        return fieldsMap
    }
}

class ComplexCustomApiType(
    name: String,
    fields: List<ComplexStructureField>
) : ComplexStructureApiType<ComplexStructureField>(name, fields) {
    override fun serializableBuilder(): TypeBuilder {
        return typeName("Serialized$name")
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

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        return methodCall {
            target = variable
            methodName = "toCustomType"
        }
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        return methodCall {
            target = variable(serializableName())
            methodName = "fromCustomType"
            addArg {
                variable
            }
        }
    }
}

data class ComplexStructureGetter(
    val name: String,
    val type: ApiTypeLogic,
    val field: String
)

data class ComplexStructureSetter(
    val name: String,
    val type: ApiTypeLogic,
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

    open fun getClassOps(): ClassBuilderOps = {
        this.name = name()
        if (c.lang is CSharp) {
            extends {
                name = "ValueObject"
            }
        }
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
                        it.type.modernDeserialize(instanceVariable(it.name))
                    })
                }
            }
        }

        addMethod {
            static = true
            returnType = this@SerializableApiType.builder()
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
                        className = this@SerializableApiType.name
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

open class ComplexValueObjectApiType(
    name: String,
    fields: List<ComplexStructureField>
) : SerializableApiType(name, fields) {
}

class EventApiType(
    name: String,
    fields: List<ComplexStructureField>
) : ComplexValueObjectApiType(name, fields) {
    override fun getClassOps(): ClassBuilderOps {
        val ops: ClassBuilderOps = super.getClassOps()
        return {
            this.apply(ops)
            if (c.lang is TypeScript) {
                extends {
                    name = "EventBusNotification"
                }
                setConstructor {
                    setBody {
                        add(hardcodedExpression("super()").asStatement())
                    }
                }

                val className = name
                addMethod {
                    name = "getName"
                    returnType = baseType(com.github.bratek20.codebuilder.core.BaseType.STRING)
                    setBody {
                        add(returnStatement {
                            string(className)
                        })
                    }
                }
                addMethod {
                    name = "getNotifier"
                    returnType = typeName("DependencyName")
                    setBody {
                        add(returnStatement {
                            hardcodedExpression("DependencyName.${moduleName()}")
                        })
                    }
                }
            }
            else {
                implements = "Event"
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
    wrappedType: ApiTypeLogic,
) : WrappedApiType(wrappedType) {
    override fun builder(): TypeBuilder {
        return listType(wrappedType.builder())
    }

    override fun serializableBuilder(): TypeBuilder {
        return listType(wrappedType.serializableBuilder())
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        if (wrappedType.name() == wrappedType.serializableName()) {
            return variable
        }
        return listOp(variable).map {
            wrappedType.modernDeserialize(variable("it"))
        }
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        if (wrappedType.name() == wrappedType.serializableName()) {
            return variable
        }
        return listOp(variable).map {
            wrappedType.modernSerialize(variable("it"))
        }
    }

    override fun getExample(): Any {
        return listOf(wrappedType.getExample())
    }
}

abstract class WrappedApiType(
    val wrappedType: ApiTypeLogic
): ApiTypeLogic()

class OptionalApiType(
    wrappedType: ApiTypeLogic,
) : WrappedApiType(wrappedType) {
    override fun builder(): TypeBuilder {
        return hardOptionalType(wrappedType.builder())
    }

    override fun serializableBuilder(): TypeBuilder {
        return softOptionalType(wrappedType.serializableBuilder())
    }

    fun unwrap(variableName: String): String {
        return languageTypes.unwrapOptional(variableName)
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

    override fun getExample(): Any {
        return wrappedType.getExample()
    }

    override fun serializableWorldType(): WorldType {
        return wrappedType.serializableWorldType();
    }
}

class EnumApiType(
    private val def: EnumDefinition,
) : ApiTypeLogic() {

    override fun builder(): TypeBuilder {
        return typeName(def.getName())
    }

    override fun serializableBuilder(): TypeBuilder {
        return baseType(com.github.bratek20.codebuilder.core.BaseType.STRING)
    }

    fun defaultValue(): String {
        val value = name() + "." + def.getValues().first()
        if (c.lang is CSharp) {
            val x = asWorldType().getPath().asHla()
            val prefixToAvoidCollision = x.getModuleName().value + "." + x.getSubmoduleName().name + "."
            return prefixToAvoidCollision + value
        }
        return value
    }

    override fun modernDeserialize(variable: ExpressionBuilder): ExpressionBuilder {
        val variableName = variable.build(c)
        return hardcodedExpression(languageTypes.deserializeEnum(name(), variableName))
    }

    override fun modernSerialize(variable: ExpressionBuilder): ExpressionBuilder {
        val variableName = variable.build(c)
        return hardcodedExpression(languageTypes.serializeEnum(variableName))
    }

    override fun getExample(): Any {
        return def.getValues().first()
    }

    override fun serializableWorldType(): WorldType {
        return PrimitiveTypesPopulator.getWorldTypeFor(BaseType.STRING)
    }
}

data class ApiCustomTypes(
    val simpleList: List<SimpleCustomApiType>,
    val complexList: List<ComplexCustomApiType>
)