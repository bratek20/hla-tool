package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.generation.impl.core.api.*
import pl.bratek20.hla.generation.impl.core.language.LanguageAssertsPattern
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes

abstract class ExpectedType<T: ApiType>(
    val api: T
) {
    lateinit var languageTypes: LanguageTypes
    lateinit var fixture: LanguageAssertsPattern

    open fun name(): String {
        return api.name()
    }

    //TODO move up?
    abstract fun defaultValue(): String

    open fun assertion(givenVariable: String, expectedVariable: String): String {
        return languageTypes.assertEquals(givenVariable, expectedVariable)
    }
}

class BaseExpectedType(
    api: BaseApiType,
) : ExpectedType<BaseApiType>(api) {
    override fun defaultValue(): String {
        return languageTypes.defaultValueForBaseType(api.name)
    }
}

open class SimpleStructureExpectedType<T: SimpleStructureApiType>(
    api: T,
    private val boxedType: BaseExpectedType,
) : StructureExpectedType<T>(api) {
    override fun defaultValue(): String {
        return boxedType.defaultValue()
    }

    override fun name(): String {
        return api.serializableName()
    }

    override fun assertion(givenVariable: String, expectedVariable: String): String {
        return languageTypes.assertEquals(api.unbox(givenVariable), expectedVariable)
    }
}

class NamedExpectedType(
    api: NamedApiType,
    boxedType: BaseExpectedType,
) : SimpleStructureExpectedType<NamedApiType>(api, boxedType)

class SimpleCustomExpectedType(
    api: SimpleCustomApiType,
    boxedType: BaseExpectedType,
) : SimpleStructureExpectedType<SimpleCustomApiType>(api, boxedType)

class ExpectedTypeField(
    val api: ApiTypeField,
    val type: ExpectedType<*>
) {
    val name = api.name

    // used by velocity
    fun assertion(givenVariable: String, expectedVariable: String): String {
        return type.assertion(api.access(givenVariable), expectedVariable)
    }
}

abstract class StructureExpectedType<T: StructureApiType>(
    api: T,
) : ExpectedType<T>(api) {
    override fun name(): String {
        return api.name()
    }

    // used by velocity
    fun funName(): String {
        return fixture.assertFunName(api.name())
    }
}

open class ComplexStructureExpectedType(
    api: ComplexStructureApiType<*>,
    val fields: List<ExpectedTypeField>
) : StructureExpectedType<ComplexStructureApiType<*>>(api) {
    override fun name(): String {
        return fixture.expectedClassType(api.name())
    }

    override fun defaultValue(): String {
        return "{}"
    }

    override fun assertion(givenVariable: String, expectedVariable: String): String {
        return fixture.complexVoAssertion(api.name(), givenVariable, expectedVariable)
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
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForList()
    }

    override fun assertion(givenVariable: String, expectedVariable: String): String {
        val entriesAssertion = languageTypes.listIndexedIteration(
            givenVariable,
            "idx",
            "entry",
            wrappedType.assertion("entry", "$expectedVariable[idx]")
        )

        val indention = " ".repeat(fixture.indentionForAssertListElements())

        return """
        |${languageTypes.assertListLength(givenVariable, expectedVariable)}
        |$indention$entriesAssertion
        """.trimMargin()
    }
}

class ListExpectedType(
    api: ListApiType,
    private val wrappedType: ExpectedType<*>,
) : ExpectedType<ListApiType>(api) {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
    }

    override fun defaultValue(): String {
        return languageTypes.defaultValueForList()
    }

    override fun assertion(givenVariable: String, expectedVariable: String): String {
        val entriesAssertion = languageTypes.listIndexedIteration(
            givenVariable,
            "idx",
            "entry",
            wrappedType.assertion("entry", "$expectedVariable[idx]")
        )

        val indention = " ".repeat(fixture.indentionForAssertListElements())

        return """
        |${languageTypes.assertListLength(givenVariable, expectedVariable)}
        |$indention$entriesAssertion
        """.trimMargin()
    }
}

class EnumExpectedType(
    api: EnumApiType,
) : ExpectedType<EnumApiType>(api) {
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
            is NamedApiType -> NamedExpectedType(type, create(type.boxedType) as BaseExpectedType)
            is ComplexVOApiType -> ComplexVOExpectedType(type, createFields(type.fields))
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

    private fun createFields(fields: List<ApiTypeField>): List<ExpectedTypeField> {
        return fields.map { ExpectedTypeField(it, create(it.type)) }
    }
}
