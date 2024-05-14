package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class ExpectedType {
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
    val domain: BaseApiType,
) : ExpectedType() {
    override fun name(): String {
        return domain.name()
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForBaseType(domain.name)
    }
}

open class SimpleStructureExpectedType(
    val domain: SimpleStructureApiType,
    val boxedType: BaseExpectedType,
) : ExpectedType() {
    override fun name(): String {
        return boxedType.name()
    }

    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }
}

class SimpleVOExpectedType(
    domain: SimpleVOApiType,
    boxedType: BaseExpectedType,
) : SimpleStructureExpectedType(domain, boxedType) {

    //TODO assignment and assertion duplicate logic?
    override fun assertion(given: String, expected: String): String {
        return languageTypes.assertEquals("${given}.value", expected)
    }
}

class SimpleCustomExpectedType(
    domain: SimpleCustomApiType,
    boxedType: BaseExpectedType,
) : SimpleStructureExpectedType(domain, boxedType) {

    //TODO fix copy paste
    override fun assignment(fieldName: String): String {
        return languageTypes.customTypeGetterName(domain.name, "value") + "($fieldName)"
    }
}

open class ComplexStructureExpectedType(
    val name: String
) : ExpectedType() {
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

class ComplexVOExpectedType(
    name: String
) : ComplexStructureExpectedType(name)

class ComplexCustomExpectedType(
    name: String
) : ComplexStructureExpectedType(name)


class PropertyExpectedType(
    name: String,
) : ComplexStructureExpectedType(name)

class ListExpectedType(
    val wrappedType: ExpectedType,
) : ExpectedType() {
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
    val view: EnumApiType,
) : ExpectedType() {
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
    fun create(type: ApiType): ExpectedType {
        val result =  when (type) {
            is BaseApiType -> BaseExpectedType(type)
            is SimpleVOApiType -> SimpleVOExpectedType(type, create(type.boxedType) as BaseExpectedType)
            is ComplexVOApiType -> ComplexVOExpectedType(type.name)
            is ListApiType -> ListExpectedType(create(type.wrappedType))
            is EnumApiType -> ExpectedEnumType(type)
            is PropertyApiType -> PropertyExpectedType(type.name)
            is SimpleCustomApiType -> SimpleCustomExpectedType(type, create(type.boxedType) as BaseExpectedType)
            is ComplexCustomApiType -> ComplexCustomExpectedType(type.name)
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

        result.languageTypes = languageTypes
        result.fixture = languageFixture

        return result
    }
}
