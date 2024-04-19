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
        return "assertThat($given).isEqualTo($expected)"
    }
}

data class SimpleVOExpectedViewType(
    val domain: SimpleVOViewType,
    val boxedType: BaseExpectedViewType
) : ExpectedViewType {
    override fun name(): String {
        return boxedType.name()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }

    override fun assertion(given: String, expected: String): String {
        return "assertThat($given.value).isEqualTo($expected)"
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
        return "assert$name($given, $expected!!)"
    }
}

data class ListExpectedViewType(
    val wrappedType: ExpectedViewType
) : ExpectedViewType {
    override fun name(): String {
        return "List<${wrappedType.name()}>"
    }

    override fun defaultValue(): String {
        return "emptyList()"
    }

    override fun assertion(given: String, expected: String): String {
        return """
        |assertThat($given).hasSize($expected!!.size)
        |        $expected!!.forEachIndexed { index, entry ->
        |            ${wrappedType.assertion("$given[index]", "entry")}
        |        }
        """.trimMargin()
    }
}

class ExpectedTypeFactory(
    private val languageTypes: LanguageTypes
) {
    fun create(type: ViewType): ExpectedViewType {
        return when (type) {
            is BaseViewType -> BaseExpectedViewType(type, languageTypes)
            is SimpleVOViewType -> SimpleVOExpectedViewType(type, create(type.boxedType) as BaseExpectedViewType)
            is ComplexVOViewType -> ComplexVOExpectedViewType(type.name)
            is ListViewType -> ListExpectedViewType(create(type.wrappedType))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}
