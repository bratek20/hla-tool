package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.apitypes.impl.*
import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.core.api.*
import com.github.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import com.github.bratek20.hla.generation.impl.core.language.LanguageTypes
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinTypes

abstract class ExpectedType<T: ApiTypeLogic>(
    val api: T
) {
    lateinit var languageTypes: LanguageTypes
    lateinit var fixture: LanguageAssertsPattern

    open fun name(): String {
        return api.name()
    }

    open fun diff(givenVariable: String, expectedVariable: String, path: String): String {
        return languageTypes.wrapWithString("$path \${$givenVariable} != \${$expectedVariable}")
    }

    open fun notEquals(givenVariable: String, expectedVariable: String): String? {
        return "$givenVariable != $expectedVariable"
    }

    override fun toString(): String {
        return "$javaClass(${name()})"
    }

    // used by velocity
    open fun diffFunName(): String {
        return fixture.diffFunName(api.name())
    }

    // used by velocity
    fun funName(): String {
        return fixture.assertFunName(api.name())
    }
}

class BaseExpectedType(
    api: BaseApiType,
) : ExpectedType<BaseApiType>(api)

open class SimpleStructureExpectedType<T: SimpleStructureApiType>(
    api: T,
    private val boxedType: BaseExpectedType,
) : StructureExpectedType<T>(api) {

    override fun name(): String {
        return api.serializableName()
    }

    fun diffBody(givenVariable: String, expectedVariable: String): String {
        val result = languageTypes.wrapWithString("\${path}value \${${api.unbox(givenVariable)}} != \${$expectedVariable}")
        return "if (${api.unbox(givenVariable)} != expected) { return $result }"
    }
}

class SimpleValueObjectExpectedType(
    api: SimpleValueObjectApiType,
    boxedType: BaseExpectedType,
) : SimpleStructureExpectedType<SimpleValueObjectApiType>(api, boxedType)

class SimpleCustomExpectedType(
    api: SimpleCustomApiType,
    boxedType: BaseExpectedType,
) : SimpleStructureExpectedType<SimpleCustomApiType>(api, boxedType)

interface ExpectedTypeField{
    fun typeName(): String
    fun name(): String
    fun diff(givenVariable: String, expectedVariable: String): String
}

open class DefaultExpectedTypeField(
    protected val api: ComplexStructureField,
    private val factory: ExpectedTypeFactory
): ExpectedTypeField {
    protected val type by lazy {
        factory.create(api.type)
    }

    override fun typeName(): String {
        return type.name()
    }

    override fun name(): String {
        return api.name
    }

    override fun diff(givenVariable: String, expectedVariable: String): String {
        val diffCall = type.diff(api.access(givenVariable), expectedVariable, "\${path}${name()}")
        val ifCondition = type.notEquals(api.access(givenVariable), expectedVariable)
        if (ifCondition == null) {
            return diffCall
        }
        return "if (${type.notEquals(api.access(givenVariable), expectedVariable)}) { ${type.languageTypes.addListElement("result", diffCall)} }"
    }
}

class OptionalEmptyExpectedTypeField(
    private val mainField: ComplexStructureField,
    private val languageTypes: LanguageTypes
): ExpectedTypeField {
    override fun typeName(): String {
        return languageTypes.mapBaseType(BaseType.BOOL)
    }

    override fun name(): String {
        return mainField.name + "Empty"
    }

    override fun diff(givenVariable: String, expectedVariable: String): String {
        var optionalEmptyCheck = languageTypes.checkOptionalEmpty(mainField.access(givenVariable))
        if (optionalEmptyCheck.contains("=")) {
            optionalEmptyCheck = "($optionalEmptyCheck)"
        }

        val element = languageTypes.wrapWithString("\${path}${mainField.name} empty \${${optionalEmptyCheck}} != \${$expectedVariable}")
        val body = languageTypes.addListElement("result", element)
        return "if (${optionalEmptyCheck} != $expectedVariable) { $body }"
    }
}

class ListExpectedTypeField(
    api: ComplexStructureField,
    factory: ExpectedTypeFactory,
): DefaultExpectedTypeField(api, factory) {
    override fun diff(givenVariable: String, expectedVariable: String): String {
        return type.diff(api.access(givenVariable), expectedVariable, "\${path}${name()}")
    }
}

abstract class ExpectedTypeWithFunName<T: ApiTypeLogic>(
    api: T,
) : ExpectedType<T>(api) {
    override fun name(): String {
        return api.name()
    }

    override fun diff(givenVariable: String, expectedVariable: String, path: String): String {
        val finalPath = languageTypes.wrapWithString("$path.")
        return "${diffFunName()}($givenVariable, $expectedVariable, $finalPath)"
    }

    override fun notEquals(givenVariable: String, expectedVariable: String): String {
        return "${diffFunName()}($givenVariable, $expectedVariable) != \"\""
    }
}

abstract class StructureExpectedType<T: StructureApiType>(
    api: T,
) : ExpectedTypeWithFunName<T>(api)

class ExternalExpectedType(
    api: ExternalApiType,
) : ExpectedTypeWithFunName<ExternalApiType>(api) {
    override fun diffFunName(): String {
        return fixture.diffFunName(api.rawName)
    }
}

open class ComplexStructureExpectedType(
    api: ComplexStructureApiType<*>,
    val fields: List<ExpectedTypeField>
) : StructureExpectedType<ComplexStructureApiType<*>>(api) {
    override fun name(): String {
        return fixture.expectedClassType(api.name())
    }

    // used by velocity
    fun defaultValue(): String {
        return "{}"
    }

    // used by velocity
    fun givenName(): String {
        return api.name()
    }

    // used by velocity
    fun expectedName(): String {
        return "Expected${api.name()}"
    }
}

class ComplexCustomExpectedType(
    api: ComplexStructureApiType<*>,
    fields: List<ExpectedTypeField>
) : ComplexStructureExpectedType(api, fields)


class PropertyExpectedType(
    api: ComplexStructureApiType<*>,
    fields: List<ExpectedTypeField>
) : ComplexStructureExpectedType(api, fields)

class OptionalExpectedType(
    api: OptionalApiType,
    private val wrappedType: ExpectedType<*>,
) : ExpectedType<OptionalApiType>(api) {
    override fun name(): String {
        if (wrappedType is ComplexStructureExpectedType) {
            return fixture.expectedClassType(wrappedType.api.name())
        }
        if (wrappedType is ListExpectedType) {
            return wrappedType.name()
        }
        return wrappedType.api.serializableName()
    }

    override fun diff(givenVariable: String, expectedVariable: String, path: String): String {
        return wrappedType.diff(api.unwrap(givenVariable), expectedVariable, path)
    }

    override fun notEquals(givenVariable: String, expectedVariable: String): String? {
        return wrappedType.notEquals(api.unwrap(givenVariable), expectedVariable)
    }
}

class ListExpectedType(
    api: ListApiType,
    private val wrappedType: ExpectedType<*>,
) : ExpectedType<ListApiType>(api) {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun notEquals(givenVariable: String, expectedVariable: String): String? {
        return null
    }

    override fun diff(givenVariable: String, expectedVariable: String, path: String): String {
        val sizeElement = "$path size \${${languageTypes.listSize(givenVariable)}} != \${${languageTypes.listSize(expectedVariable)}}"
        var sizeBody = languageTypes.addListElement("result", languageTypes.wrapWithString(sizeElement))
        if (languageTypes is KotlinTypes) {
           sizeBody += "; return@let"
        }
        val sizePart = "if (${languageTypes.listSize(givenVariable)} != ${languageTypes.listSize(expectedVariable)}) { $sizeBody }"

        val element = wrappedType.diff("entry", "$expectedVariable[idx]", "$path[\${idx}]")
        val body = languageTypes.addListElement("result", element)
        val full = "if (${wrappedType.notEquals("entry", "$expectedVariable[idx]")}) { $body }"
        val entriesAssertion = languageTypes.listIndexedIteration(
            givenVariable,
            "idx",
            "entry",
            full
        )

        val indention = " ".repeat(fixture.indentionForAssertListElements())

        return """
        |${sizePart}
        |$indention$entriesAssertion
        """.trimMargin()
    }
}

class EnumExpectedType(
    api: EnumApiType,
) : ExpectedType<EnumApiType>(api) {
    fun diffBody(givenVariable: String, expectedVariable: String): String {
        val result = languageTypes.wrapWithString("\${path}value \${${api.serialize(givenVariable)}} != \${$expectedVariable}")
        return "if (${givenVariable} != ${api.deserialize(expectedVariable)}) { return $result }"
    }

    override fun name(): String {
        return api.serializableName()
    }

    override fun diff(givenVariable: String, expectedVariable: String, path: String): String {
        val finalPath = languageTypes.wrapWithString("$path.")
        return "${diffFunName()}($givenVariable, $expectedVariable, $finalPath)"
    }

    override fun notEquals(givenVariable: String, expectedVariable: String): String {
        return "${diffFunName()}($givenVariable, $expectedVariable) != \"\""
    }
}

class ExpectedTypeFactory(
    private val c: ModuleGenerationContext
) {
    fun create(type: ApiTypeLogic): ExpectedType<*> {
        val result =  when (type) {
            is BaseApiType -> BaseExpectedType(type)
            is SimpleValueObjectApiType -> SimpleValueObjectExpectedType(type, create(type.boxedType) as BaseExpectedType)
            is OptionalApiType -> OptionalExpectedType(type, create(type.wrappedType))
            is ListApiType -> ListExpectedType(type, create(type.wrappedType))
            is EnumApiType -> EnumExpectedType(type)
            is SerializableApiType -> PropertyExpectedType(type, createFields(type.fields))
            is SimpleCustomApiType -> SimpleCustomExpectedType(type, create(type.boxedType) as BaseExpectedType)
            is ComplexCustomApiType -> ComplexCustomExpectedType(type, createFields(type.fields))
            is ExternalApiType -> ExternalExpectedType(type)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.languageTypes = type.languageTypes
        result.fixture = c.language.assertsFixture()

        return result
    }

    private fun createFields(fields: List<ComplexStructureField>): List<ExpectedTypeField> {
        return fields.map {
            when (it.type) {
                is OptionalApiType -> {
                    listOf(
                        OptionalEmptyExpectedTypeField(it, c.language.types()),
                        DefaultExpectedTypeField(it, this)
                    )
                }
                is ListApiType -> {
                    listOf(
                        ListExpectedTypeField(it, this)
                    )
                }
                else -> {
                    listOf(DefaultExpectedTypeField(it, this))
                }
            }
        }.flatten()
    }
}
