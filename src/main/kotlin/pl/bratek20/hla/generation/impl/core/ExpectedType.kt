package pl.bratek20.hla.generation.impl.core

interface ExpectedType {
    fun toView(): String

    fun defaultValue(): String

    fun assertion(given: String, expected: String): String
}

data class BaseExpectedType(
    val domain: BaseViewType,
    val languageTypes: LanguageTypes
) : ExpectedType {
    override fun toView(): String {
        return domain.name()
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForBaseType(domain.name)
    }

    override fun assertion(given: String, expected: String): String {
        return ""
    }
}

data class SimpleVOExpectedType(
    val domain: SimpleVOViewType,
    val boxedType: BaseExpectedType
) : ExpectedType {
    override fun toView(): String {
        return boxedType.toView()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }

    override fun assertion(given: String, expected: String): String {
        return ""
    }
}

data class ComplexVOExpectedType(
    val name: String
) : ExpectedType {
    override fun toView(): String {
        return "(Expected$name.() -> Unit)"
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun assertion(given: String, expected: String): String {
        return "assert$name($given, $expected!!)"
    }
}

data class ListExpectedType(
    val wrappedType: ExpectedType
) : ExpectedType {
    override fun toView(): String {
        return "List<${wrappedType.toView()}>"
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
    fun create(type: ViewType): ExpectedType {
        return when (type) {
            is BaseViewType -> BaseExpectedType(type, languageTypes)
            is SimpleVOViewType -> SimpleVOExpectedType(type, create(type.boxedType) as BaseExpectedType)
            is ComplexVOViewType -> ComplexVOExpectedType(type.name)
            is ListViewType -> ListExpectedType(create(type.wrappedType))
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
}
