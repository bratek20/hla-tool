package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.apitypes.api.ApiType
import com.github.bratek20.hla.apitypes.impl.*
import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.generation.impl.core.api.*
import com.github.bratek20.hla.generation.impl.core.language.LanguageBuildersPattern
import com.github.bratek20.hla.generation.impl.core.language.LanguageTypes
import com.github.bratek20.hla.generation.impl.languages.csharp.CSharpTypes
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinTypes
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
    fun build(variableName: String): String = modernBuild(variable(variableName)).build(api.languageTypes.context())

    abstract fun builder(): TypeBuilder

    abstract fun defaultValueBuilder(): ExpressionBuilder

    abstract fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder

    open fun emptyValueBuilder(): ExpressionBuilder {
        return defaultValueBuilder()
    }

    protected fun emptyValueForClassType(): ExpressionBuilder {
        return if(api.languageTypes is KotlinTypes) emptyLambda() else nullValue()
    }
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

    override fun emptyValueBuilder(): ExpressionBuilder {
        return when(api.name) {
            BaseType.STRING -> emptyString()
            BaseType.INT -> const(0)
            BaseType.BOOL -> falseValue()
            BaseType.VOID -> error("Void does not have empty value")
            BaseType.ANY -> emptyValueForClassType()
            BaseType.DOUBLE -> const(0)
            BaseType.LONG -> const(0)
            BaseType.STRUCT -> emptyValueForClassType()
        }
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

abstract class SimpleStructureDefType<T: SimpleStructureApiType>(
    api: T,
    private val boxedType: BaseDefType
) : StructureDefType<T>(api) {
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

    override fun emptyValueBuilder(): ExpressionBuilder {
        return boxedType.emptyValueBuilder()
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
    val name = if(api.isKotlinPrivateWord(api.name)) "`${api.name}`"  else api.name

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
        if (type is ListDefType) {
            return type.defaultValueBuilder()
        }
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

    override fun builder(): TypeBuilder {
        if (languageTypes is TypeScriptTypes) {
            return typeName(pattern.defClassType(api.name()))
        }
        return lambdaType(typeName(defName()))
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return emptyLambda(1)
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        val variableName = variable.build(api.languageTypes.context())
        return functionCall {
            name = pattern.complexVoDefConstructor(api.name(), variableName)
            addArg {
                variable
            }
        }
    }

    override fun emptyValueBuilder(): ExpressionBuilder {
        return emptyValueForClassType()
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

    override fun builder(): TypeBuilder {
        return softOptionalType(wrappedType.builder())
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return nullValue()
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        val asOptional = hardOptional(wrappedType.builder()) {
            variable
        }

        val mapping = wrappedType.modernBuild(variable("it"))
        if (mapping.build(api.languageTypes.context()) == "it") {
            return asOptional
        }

        return optionalOp(asOptional).map {
            mapping
        }
    }
}

class ListDefType(
    api: ListApiType,
    val wrappedType: DefType<*>
) : DefType<ListApiType>(api) {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return emptyImmutableList(wrappedType.builder())
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        if (wrappedType is BaseDefType) {
            return variable
        }
        return listOp(variable).map { wrappedType.modernBuild(variable("it")) }
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

open class ToDoDefType<T: ApiTypeLogic>(
    api: T
) : DefType<T>(api) {
    override fun name(): String {
        return api.name()
    }

    override fun builder(): TypeBuilder {
        return api.builder()
    }

    override fun defaultValueBuilder(): ExpressionBuilder {
        return hardcodedExpression("TODO()")
    }

    override fun modernBuild(variable: ExpressionBuilder): ExpressionBuilder {
        return hardcodedExpression("TODO()")
    }
}

class ExternalDefType(
    api: ExternalApiType
) : ToDoDefType<ExternalApiType>(api)

class InterfaceDefType(
    api: InterfaceApiType
) : ToDoDefType<InterfaceApiType>(api)

class DefTypeFactory(
    private val pattern: LanguageBuildersPattern
) {
    fun create(type: ApiType): DefType<*> {
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
            is InterfaceApiType -> InterfaceDefType(type)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.pattern = pattern

        return result
    }

    private fun createFields(fields: List<ComplexStructureField>): List<DefField> {
        return fields.map { DefField(it, this) }
    }
}
