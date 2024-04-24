package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

interface ExpectedViewType {
    fun name(): String

    fun defaultValue(): String

    fun assertion(given: String, expected: String): String
}

data class BaseExpectedViewType(
    val domain: BaseViewType,
    val languageTypes: LanguageTypes
) : ExpectedViewType {
    override fun name(): String {
        return domain.name()
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForBaseType(domain.name)
    }

    override fun assertion(given: String, expected: String): String {
        return languageTypes.assertEquals(given, expected)
    }
}

data class SimpleVOExpectedViewType(
    val domain: SimpleVOViewType,
    val boxedType: BaseExpectedViewType,
    val languageTypes: LanguageTypes
) : ExpectedViewType {
    override fun name(): String {
        return boxedType.name()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }

    override fun assertion(given: String, expected: String): String {
        return languageTypes.assertEquals("${given}.value", expected)
    }
}

data class ComplexVOExpectedViewType(
    val name: String,
    val types: LanguageTypes,
    val fixture: LanguageAssertsPattern
) : ExpectedViewType {
    override fun name(): String {
        return fixture.expectedClassType(name)
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun assertion(given: String, expected: String): String {
        return fixture.complexVoAssertion(name, given, expected)
    }
}

data class ListExpectedViewType(
    val wrappedType: ExpectedViewType,
    val types: LanguageTypes,
    val fixture: LanguageAssertsPattern
) : ExpectedViewType {
    override fun name(): String {
        return types.wrapWithList(wrappedType.name())
    }

    override fun defaultValue(): String {
        return types.defaultValueForList()
    }

    override fun assertion(given: String, expected: String): String {
        val entriesAssertion = types.listIndexedIteration(
            given,
            "idx",
            "entry",
            wrappedType.assertion("entry", "$expected[idx]")
        )

        val indention = " ".repeat(fixture.indentionForAssertListElements())

        return """
        |${types.assertListLength(given, expected)}
        |$indention$entriesAssertion
        """.trimMargin()
    }
}

class ExpectedTypeFactory(
    private val languageTypes: LanguageTypes,
    private val languageFixture: LanguageAssertsPattern,
) {
    fun create(type: ViewType): ExpectedViewType {
        return when (type) {
            is BaseViewType -> BaseExpectedViewType(type, languageTypes)
            is SimpleVOViewType -> SimpleVOExpectedViewType(type, create(type.boxedType) as BaseExpectedViewType, languageTypes)
            is ComplexVOViewType -> ComplexVOExpectedViewType(type.name, languageTypes, languageFixture)
            is ListViewType -> ListExpectedViewType(create(type.wrappedType), languageTypes, languageFixture)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}
