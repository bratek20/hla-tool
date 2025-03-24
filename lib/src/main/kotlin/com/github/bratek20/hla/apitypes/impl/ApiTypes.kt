package com.github.bratek20.hla.apitypes.impl

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.apitypes.api.ApiType
import com.github.bratek20.hla.definitions.api.*
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.api.ComplexStructureField
import com.github.bratek20.hla.generation.impl.core.language.LanguageTypes
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinTypes
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.hlatypesworld.impl.B20FrontendTypesPopulator
import com.github.bratek20.hla.hlatypesworld.impl.PrimitiveTypesPopulator
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.utils.pascalToCamelCase

abstract class ApiTypeLogic: ApiType {
    protected val c
        get() = languageTypes.context()

    lateinit var languageTypes: LanguageTypes
    var typeModule: ModuleDefinition? = null

    fun init(languageTypes: LanguageTypes, typeModule: ModuleDefinition?) {
        this.languageTypes = languageTypes
        this.typeModule = typeModule
    }

    protected fun moduleName(): String {
        return typeModule?.getName()?.value ?: throw IllegalStateException("No module set for type $this")
    }

    override fun asWorldType(): WorldType {
        //TODO-REF
        if (this is BaseApiType) {
            return WorldType.create(
                name = WorldTypeName(this.name.name.lowercase()),
                path = PrimitiveTypesPopulator.path
            )
        }
        //TODO-FIX it should not be needed to hardcode it like that
        if (name() == "EmptyModel") {
            return B20FrontendTypesPopulator.emptyModelType
        }
        return asOptHlaType() ?: throw IllegalStateException("No HlaType for type $this")
    }

    private fun asOptHlaType(): WorldType? {
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

    companion object {
        fun isVoid(type: ApiType): Boolean {
            return type is BaseApiType && type.name == BaseType.VOID
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

fun extractExampleValueForBaseType(attributes: List<Attribute>): String? {
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
                        it.type.modernDeserialize(variable(it.name))
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
}

data class ApiCustomTypes(
    val simpleList: List<SimpleCustomApiType>,
    val complexList: List<ComplexCustomApiType>
)