package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class ExpectedType<T: ApiType>(
    val api: T
) {
    lateinit var languageTypes: LanguageTypes
    lateinit var fixture: LanguageAssertsPattern

    abstract fun name(): String

    abstract fun defaultValue(): String

    open fun assertion(given: String, expected: String): String {
        return languageTypes.assertEquals(given, expected)
    }

    open fun assignment(fieldName: String): String {
        return fieldName
    }
}

class BaseExpectedType(
    api: BaseApiType,
) : ExpectedType<BaseApiType>(api) {
    override fun name(): String {
        return api.name()
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForBaseType(api.name)
    }
}

open class SimpleStructureExpectedType<T: SimpleStructureApiType>(
    api: T,
    val boxedType: BaseExpectedType,
) : ExpectedType<T>(api) {
    override fun name(): String {
        return boxedType.name()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }
}

class SimpleVOExpectedType(
    api: SimpleVOApiType,
    boxedType: BaseExpectedType,
) : SimpleStructureExpectedType<SimpleVOApiType>(api, boxedType) {

    //TODO assignment and assertion duplicate logic?
    override fun assertion(given: String, expected: String): String {
        return languageTypes.assertEquals("${given}.value", expected)
    }
}

class SimpleCustomExpectedType(
    api: SimpleCustomApiType,
    boxedType: BaseExpectedType,
) : SimpleStructureExpectedType<SimpleCustomApiType>(api, boxedType) {

    //TODO fix copy paste
    override fun assignment(fieldName: String): String {
        return languageTypes.customTypeGetterCall(api.name, "value") + "($fieldName)"
    }
}

class ExpectedField(
    val name: String,
    val type: ExpectedType<*>
)

open class ComplexStructureExpectedType(
    api: ComplexStructureApiType,
    val fields: List<ExpectedField>
) : ExpectedType<ComplexStructureApiType>(api) {
    override fun name(): String {
        return fixture.expectedClassType(api.name())
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun assertion(given: String, expected: String): String {
        return fixture.complexVoAssertion(api.name(), given, expected)
    }
}

class ComplexVOExpectedType(
    api: ComplexStructureApiType,
    fields: List<ExpectedField>
) : ComplexStructureExpectedType(api, fields)

class ComplexCustomExpectedType(
    api: ComplexStructureApiType,
    fields: List<ExpectedField>
) : ComplexStructureExpectedType(api, fields)


class PropertyExpectedType(
    api: ComplexStructureApiType,
    fields: List<ExpectedField>
) : ComplexStructureExpectedType(api, fields)

class ListExpectedType(
    api: ListApiType,
    val wrappedType: ExpectedType<*>,
) : ExpectedType<ListApiType>(api) {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForList()
    }

    override fun assertion(given: String, expected: String): String {
        val entriesAssertion = languageTypes.listIndexedIteration(
            given,
            "idx",
            "entry",
            wrappedType.assertion("entry", "$expected[idx]")
        )

        val indention = " ".repeat(fixture.indentionForAssertListElements())

        return """
        |${languageTypes.assertListLength(given, expected)}
        |$indention$entriesAssertion
        """.trimMargin()
    }
}

class ExpectedEnumType(
    api: EnumApiType,
) : ExpectedType<EnumApiType>(api) {
    override fun name(): String {
        return api.name()
    }

    override fun defaultValue(): String {
        return api.defaultValue()
    }
}

class ExpectedTypeFactory(
    private val languageTypes: LanguageTypes,
    private val languageFixture: LanguageAssertsPattern,
) {
    fun create(type: ApiType): ExpectedType<*> {
        val result =  when (type) {
            is BaseApiType -> BaseExpectedType(type)
            is SimpleVOApiType -> SimpleVOExpectedType(type, create(type.boxedType) as BaseExpectedType)
            is ComplexVOApiType -> ComplexVOExpectedType(type, createFields(type.fields))
            is ListApiType -> ListExpectedType(type, create(type.wrappedType))
            is EnumApiType -> ExpectedEnumType(type)
            is PropertyApiType -> PropertyExpectedType(type, createFields(type.fields))
            is SimpleCustomApiType -> SimpleCustomExpectedType(type, create(type.boxedType) as BaseExpectedType)
            is ComplexCustomApiType -> ComplexCustomExpectedType(type, createFields(type.fields))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.languageTypes = languageTypes
        result.fixture = languageFixture

        return result
    }

    private fun createFields(fields: List<ApiTypeField>): List<ExpectedField> {
        return fields.map { ExpectedField(it.name, create(it.type)) }
    }
}
