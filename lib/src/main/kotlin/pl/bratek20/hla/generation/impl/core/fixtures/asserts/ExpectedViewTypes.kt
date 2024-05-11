package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class ExpectedViewType(
    protected val languageTypes: LanguageTypes
) {
    abstract fun name(): String

    abstract fun defaultValue(): String

    open fun assertion(given: String, expected: String): String {
        return languageTypes.assertEquals(given, expected)
    }
}

class BaseExpectedViewType(
    val domain: BaseViewType,
    languageTypes: LanguageTypes,
) : ExpectedViewType(languageTypes) {
    override fun name(): String {
        return domain.name()
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForBaseType(domain.name)
    }
}

class SimpleVOExpectedViewType(
    val domain: SimpleVOViewType,
    private val boxedType: BaseExpectedViewType,
    languageTypes: LanguageTypes,
) : ExpectedViewType(languageTypes) {
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

class ComplexVOExpectedViewType(
    val name: String,
    private val fixture: LanguageAssertsPattern,
    languageTypes: LanguageTypes,
) : ExpectedViewType(languageTypes) {
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

class ListExpectedViewType(
    val wrappedType: ExpectedViewType,
    val fixture: LanguageAssertsPattern,
    languageTypes: LanguageTypes,
) : ExpectedViewType(languageTypes) {
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

class ExpectedEnumViewType(
    val view: EnumViewType,
    languageTypes: LanguageTypes
) : ExpectedViewType(languageTypes) {
    override fun name(): String {
        return view.name()
    }

    override fun defaultValue(): String {
        return view.defaultValue()
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
            is ComplexVOViewType -> ComplexVOExpectedViewType(type.name, languageFixture, languageTypes)
            is ListViewType -> ListExpectedViewType(create(type.wrappedType), languageFixture, languageTypes)
            is EnumViewType -> ExpectedEnumViewType(type, languageTypes)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}
