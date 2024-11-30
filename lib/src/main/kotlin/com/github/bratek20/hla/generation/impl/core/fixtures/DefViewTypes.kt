package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.apitypes.impl.*
import com.github.bratek20.hla.generation.impl.core.api.*
import com.github.bratek20.hla.generation.impl.core.language.LanguageBuildersPattern
import com.github.bratek20.hla.generation.impl.core.language.LanguageTypes
import com.github.bratek20.hla.generation.impl.languages.csharp.CSharpTypes
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptTypes
import com.github.bratek20.utils.pascalToCamelCase

abstract class DefType<T: ApiTypeLogic>(
    val api: T
) {
    protected val languageTypes: LanguageTypes
        get() = api.languageTypes

    lateinit var pattern: LanguageBuildersPattern

    abstract fun name(): String

    @Deprecated("Use defaultValueBuilder() instead")
    fun defaultValue(): String = defaultValueBuilder().build(api.languageTypes.context())

    @Deprecated("Use modernBuild() instead")
    open fun build(variableName: String): String = modernBuild(variable(variableName)).build(api.languageTypes.context())

    abstract fun builder(): TypeBuilder

    abstract fun defaultValueBuilder(): ExpressionBuilder

    abstract fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder
}

class BaseDefType(
    api: BaseApiType,
) : DefType<BaseApiType>(api) {
    override fun name(): String {
        return api.name()
    }

    override fun builder(): TypeBuilder {
        return api.serializableBuilder()
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return const(api.languageTypes.defaultValueForBaseType(api.name))
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        return variable
    }
}

abstract class StructureDefType<T: StructureApiType>(
    api: T,
) : DefType<T>(api) {
    fun funName(): String {
        if (languageTypes is CSharpTypes) {
            return "build" + api.name()
        }
        return pascalToCamelCase(api.name())
    }
}

class ExternalDefType(
    api: ExternalApiType,
) : DefType<ExternalApiType>(api) {
    override fun name(): String {
        return languageTypes.wrapWithOptional(pascalToCamelCase(api.name()))
    }

    override fun builder(): TypeBuilder {
        return api.serializableBuilder()
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return nullValue()
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        return variable
    }
}

abstract class SimpleStructureDefType<T: SimpleStructureApiType>(
    api: T,
    private val boxedType: BaseDefType
) : StructureDefType<T>(api) {
    override fun build(variableName: String): String {
        return api.deserialize(variableName)
    }

    override fun name(): String {
        return boxedType.name()
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return api.exampleValueBuilder() ?: boxedType.defaultValueBuilder()
    }

    override fun builder(): TypeBuilder {
        return boxedType.builder()
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        return api.modernDeserialize(variable)
    }
}

class SimpleVODefType(
    api: SimpleValueObjectApiType,
    boxedType: BaseDefType
) : SimpleStructureDefType<SimpleValueObjectApiType>(api, boxedType) {
}

class SimpleCustomDefType(
    api: SimpleCustomApiType,
    boxedType: BaseDefType
) : SimpleStructureDefType<SimpleCustomApiType>(api, boxedType) {
}

open class DefField(
    val api: ComplexStructureField,
    private val factory: DefTypeFactory
) {
    val name = api.name

    val type by lazy {
        factory.create(api.type)
    }

    open fun build(variableName: String): String {
        return type.build("${variableName}.${name}")
    }

    open fun build(): String {
        if (api.type.languageTypes is TypeScriptTypes) {
            return type.build("final_$name")
        }
        return type.build(name)
    }

    fun modernBuild(variableName: String): ExpressionBuilder {
        return type.modernBuild(getterFieldAccess {
            objectRef = variable(variableName)
            fieldName = name
        })
    }

    // used by velocity
    @Deprecated("Use defaultValueBuilder() instead")
    fun defaultValue(): String {
        return defaultValueBuilder().build(api.factory.languageTypes.context())
    }

    fun defaultValueBuilder(): ExpressionBuilder {
        return api.exampleValueBuilder() ?:
            api.defaultSerializedValueBuilder() ?:
            type.defaultValueBuilder()
    }
}

open class ComplexStructureDefType(
    api: ComplexStructureApiType<*>,
    val fields: List<DefField>
) : StructureDefType<ComplexStructureApiType<*>>(api) {
    fun defName(): String {
        return api.name() + "Def"
    }

    override fun name(): String {
        return pattern.defClassType(api.name());
    }

    override fun build(variableName: String): String {
        return pattern.complexVoDefConstructor(api.name(), variableName)
    }

    override fun builder(): TypeBuilder {
        return lambdaType(typeName(defName()))
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return emptyLambda(1)
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        return methodCall {
            methodName = funName()
            addArg {
                variable
            }
        }
    }
}

class ComplexCustomDefType(
    api: ComplexStructureApiType<*>,
    fields: List<DefField>
) : ComplexStructureDefType(api, fields)

class OptionalDefType(
    api: OptionalApiType,
    val wrappedType: DefType<*>
) : DefType<OptionalApiType>(api) {
    override fun name(): String {
        if (wrappedType is ComplexStructureDefType) {
            return pattern.defOptionalComplexType(wrappedType.api.name())
        }
        return languageTypes.wrapWithSoftOptional(wrappedType.name())
    }

    override fun build(variableName: String): String {
        val mapping = wrappedType.build("it")
        if (mapping == "it") {
            return pattern.mapOptionalDefBaseElement(variableName)
        }
        return pattern.mapOptionalDefElement(variableName, "it", mapping)
    }

    override fun builder(): TypeBuilder {
        return softOptionalType(wrappedType.builder())
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return nullValue()
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        return hardcodedExpression("TODO")
    }
}

class ListDefType(
    api: ListApiType,
    val wrappedType: DefType<*>
) : DefType<ListApiType>(api) {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun build(variableName: String): String {
        if (wrappedType is BaseDefType) {
            return variableName
        }
        return languageTypes.mapListElements(variableName, "it", wrappedType.build("it"))
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return emptyImmutableList(wrappedType.builder())
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        return hardcodedExpression("TODO")
    }

    override fun builder(): TypeBuilder {
        return listType(wrappedType.builder())
    }
}

class EnumDefType(
    api: EnumApiType
) : DefType<EnumApiType>(api) {
    override fun name(): String {
        return api.serializableName()
    }

    override fun builder(): TypeBuilder {
        return api.serializableBuilder()
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return api.modernSerialize(variable(api.defaultValue()))
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        return api.modernDeserialize(variable)
    }
}

class DefTypeFactory(
    private val pattern: LanguageBuildersPattern
) {
    fun create(type: ApiTypeLogic): DefType<*> {
        val result = when (type) {
            is BaseApiType -> BaseDefType(type)
            is SimpleValueObjectApiType -> SimpleVODefType(type, create(type.boxedType) as BaseDefType)
            is OptionalApiType -> OptionalDefType(type, create(type.wrappedType))
            is ListApiType -> ListDefType(type, create(type.wrappedType))
            is EnumApiType -> EnumDefType(type)
            is SimpleCustomApiType -> SimpleCustomDefType(type, create(type.boxedType) as BaseDefType)
            is ComplexCustomApiType -> ComplexCustomDefType(type, createFields(type.fields))
            is SerializableApiType -> ComplexStructureDefType(type, createFields(type.fields))
            is ExternalApiType -> ExternalDefType(type)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.pattern = pattern

        return result
    }

    private fun createFields(fields: List<ComplexStructureField>): List<DefField> {
        return fields.map { DefField(it, this) }
    }
}
