package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.generation.impl.core.api.*
import com.github.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import com.github.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class ExpectedType<T: ApiType>(
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

    open fun notEquals(givenVariable: String, expectedVariable: String): String {
        return "$givenVariable != $expectedVariable"
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
        val x = type.diff(api.access(givenVariable), expectedVariable, "\${path}${name()}")
        return "if (${type.notEquals(api.access(givenVariable), expectedVariable)}) { ${type.languageTypes.addListElement("result", x)} }"
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
        val element = languageTypes.wrapWithString("\${path}${mainField.name} empty \${($givenVariable.${mainField.name} == null) != $expectedVariable} != \${$expectedVariable}")
        val body = languageTypes.addListElement("result", element)
        return "if (($givenVariable.${mainField.name} == null) != $expectedVariable) { $body }"
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

abstract class StructureExpectedType<T: StructureApiType>(
    api: T,
) : ExpectedType<T>(api) {
    override fun name(): String {
        return api.name()
    }

    // used by velocity
    fun diffFunName(): String {
        return fixture.diffFunName(api.name())
    }

    // used by velocity
    fun funName(): String {
        return fixture.assertFunName(api.name())
    }

    override fun diff(givenVariable: String, expectedVariable: String, path: String): String {
        val finalPath = languageTypes.wrapWithString("$path.")
        return "${diffFunName()}($givenVariable, $expectedVariable, $finalPath)"
    }

    override fun notEquals(givenVariable: String, expectedVariable: String): String {
        return "${diffFunName()}($givenVariable, $expectedVariable) != \"\""
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

class ComplexVOExpectedType(
    api: ComplexStructureApiType<*>,
    fields: List<ExpectedTypeField>
) : ComplexStructureExpectedType(api, fields)

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
        if (wrappedType is BaseExpectedType) {
            return wrappedType.name()
        }
        if (wrappedType is SimpleValueObjectExpectedType) {
            return wrappedType.api.boxedType.name()
        }
        return fixture.expectedClassType(wrappedType.api.name())
    }

    override fun diff(givenVariable: String, expectedVariable: String, path: String): String {
        return wrappedType.diff(api.unwrap(givenVariable), expectedVariable, path)
    }

    override fun notEquals(givenVariable: String, expectedVariable: String): String {
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

    override fun notEquals(givenVariable: String, expectedVariable: String): String {
        return "notEquals not needed";
    }

    override fun diff(givenVariable: String, expectedVariable: String, path: String): String {
        val sizeElement = "$path size \${${languageTypes.listSize(givenVariable)}} != \${${languageTypes.listSize(expectedVariable)}}"
        val sizeBody = languageTypes.addListElement("result", languageTypes.wrapWithString(sizeElement))
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
) : ExpectedType<EnumApiType>(api)

class ExpectedTypeFactory(
    private val languageTypes: LanguageTypes,
    private val languageFixture: LanguageAssertsPattern,
) {
    fun create(type: ApiType): ExpectedType<*> {
        val result =  when (type) {
            is BaseApiType -> BaseExpectedType(type)
            is SimpleValueObjectApiType -> SimpleValueObjectExpectedType(type, create(type.boxedType) as BaseExpectedType)
            is OptionalApiType -> OptionalExpectedType(type, create(type.wrappedType))
            is ListApiType -> ListExpectedType(type, create(type.wrappedType))
            is EnumApiType -> EnumExpectedType(type)
            is SerializableApiType -> PropertyExpectedType(type, createFields(type.fields))
            is SimpleCustomApiType -> SimpleCustomExpectedType(type, create(type.boxedType) as BaseExpectedType)
            is ComplexCustomApiType -> ComplexCustomExpectedType(type, createFields(type.fields))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.languageTypes = languageTypes
        result.fixture = languageFixture

        return result
    }

    private fun createFields(fields: List<ComplexStructureField>): List<ExpectedTypeField> {
        return fields.map {
            if (it.type is OptionalApiType) {
                listOf(
                    OptionalEmptyExpectedTypeField(it, languageTypes),
                    DefaultExpectedTypeField(it, this)
                )
            }
            if (it.type is ListApiType) {
                listOf(
                    ListExpectedTypeField(it, this)
                )
            }
            else {
                listOf(DefaultExpectedTypeField(it, this))
            }
        }.flatten()
    }
}
