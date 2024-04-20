package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.generation.impl.core.domain.*

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
    val name: String
) : ExpectedViewType {
    override fun name(): String {
        return "(Expected$name.() -> Unit)"
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun assertion(given: String, expected: String): String {
        return "assert$name($given, $expected)"
    }
}

data class ListExpectedViewType(
    val wrappedType: ExpectedViewType,
    val languageTypes: LanguageTypes
) : ExpectedViewType {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForList()
    }

    override fun assertion(given: String, expected: String): String {
        val entriesAssertion = languageTypes.arrayIndexedIteration(
            given,
            "idx",
            "entry",
            wrappedType.assertion("entry", "$expected[idx]")
        )

        return """
        |${languageTypes.assertArraysLength(given, expected)}
        |        $entriesAssertion
        """.trimMargin()
    }
}

class ExpectedTypeFactory(
    private val languageTypes: LanguageTypes
) {
    fun create(type: ViewType): ExpectedViewType {
        return when (type) {
            is BaseViewType -> BaseExpectedViewType(type, languageTypes)
            is SimpleVOViewType -> SimpleVOExpectedViewType(type, create(type.boxedType) as BaseExpectedViewType, languageTypes)
            is ComplexVOViewType -> ComplexVOExpectedViewType(type.name)
            is ListViewType -> ListExpectedViewType(create(type.wrappedType), languageTypes)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}
