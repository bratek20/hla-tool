package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class ExpectedViewType {
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

class BaseExpectedViewType(
    val domain: BaseViewType,
) : ExpectedViewType() {
    override fun name(): String {
        return domain.name()
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForBaseType(domain.name)
    }
}

open class SimpleStructureExpectedViewType(
    val domain: SimpleStructureViewType,
    val boxedType: BaseExpectedViewType,
) : ExpectedViewType() {
    override fun name(): String {
        return boxedType.name()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }
}

class SimpleVOExpectedViewType(
    domain: SimpleVOViewType,
    boxedType: BaseExpectedViewType,
) : SimpleStructureExpectedViewType(domain, boxedType) {

    //TODO assignment and assertion duplicate logic?
    override fun assertion(given: String, expected: String): String {
        return languageTypes.assertEquals("${given}.value", expected)
    }
}

class SimpleCustomExpectedViewType(
    domain: SimpleCustomViewType,
    boxedType: BaseExpectedViewType,
) : SimpleStructureExpectedViewType(domain, boxedType) {

    //TODO fix copy paste
    override fun assignment(fieldName: String): String {
        return "get${domain.name}Value($fieldName)"
    }
}

open class ComplexStructureExpectedViewType(
    val name: String
) : ExpectedViewType() {
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

class ComplexVOExpectedViewType(
    name: String
) : ComplexStructureExpectedViewType(name)

class ComplexCustomExpectedViewType(
    name: String
) : ComplexStructureExpectedViewType(name)

class ListExpectedViewType(
    val wrappedType: ExpectedViewType,
) : ExpectedViewType() {
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
) : ExpectedViewType() {
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
        val result =  when (type) {
            is BaseViewType -> BaseExpectedViewType(type)
            is SimpleVOViewType -> SimpleVOExpectedViewType(type, create(type.boxedType) as BaseExpectedViewType)
            is ComplexVOViewType -> ComplexVOExpectedViewType(type.name)
            is ListViewType -> ListExpectedViewType(create(type.wrappedType))
            is EnumViewType -> ExpectedEnumViewType(type)
            is SimpleCustomViewType -> SimpleCustomExpectedViewType(type, create(type.boxedType) as BaseExpectedViewType)
            is ComplexCustomViewType -> ComplexCustomExpectedViewType(type.name)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.languageTypes = languageTypes
        result.fixture = languageFixture

        return result
    }
}
