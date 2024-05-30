package pl.bratek20.hla.generation.impl.core.fixtures.asserts

import pl.bratek20.hla.definitions.api.BaseType
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

    open fun assertion(givenVariable: String, expectedVariable: String): String {
        return languageTypes.assertEquals(givenVariable, expectedVariable)
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

interface ExpectedTypeField{
    fun typeName(): String
    fun name(): String
    fun assertion(givenVariable: String, expectedVariable: String): String
}

class DefaultExpectedTypeField(
    private val api: ApiTypeField,
    private val type: ExpectedType<*>
): ExpectedTypeField {
    override fun typeName(): String {
        return type.name()
    }

    override fun name(): String {
        return api.name
    }

    override fun assertion(givenVariable: String, expectedVariable: String): String {
        return type.assertion(api.access(givenVariable), expectedVariable)
    }
}

class OptionalEmptyExpectedTypeField(
    private val mainField: ApiTypeField,
    private val languageTypes: LanguageTypes
): ExpectedTypeField {
    override fun typeName(): String {
        return languageTypes.mapBaseType(BaseType.BOOL)
    }

    override fun name(): String {
        return mainField.name + "Empty"
    }

    override fun assertion(givenVariable: String, expectedVariable: String): String {
        val emptyCheck = languageTypes.checkOptionalEmpty("$givenVariable.${mainField.name}")
        return languageTypes.assertEquals(emptyCheck, expectedVariable)
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

    override fun assertion(givenVariable: String, expectedVariable: String): String {
        return fixture.complexVoAssertion(api.name(), givenVariable, expectedVariable)
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
        return fixture.expectedClassType(wrappedType.api.name())
    }

    override fun assertion(givenVariable: String, expectedVariable: String): String {
        return wrappedType.assertion(api.unwrap(givenVariable), expectedVariable)
    }
}

class ListExpectedType(
    api: ListApiType,
    private val wrappedType: ExpectedType<*>,
) : ExpectedType<ListApiType>(api) {
    override fun name(): String {
        return languageTypes.wrapWithList(wrappedType.name())
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
) : ExpectedType<EnumApiType>(api)

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
        return fields.map {
            if (it.type is OptionalApiType) {
                listOf(
                    OptionalEmptyExpectedTypeField(it, languageTypes),
                    DefaultExpectedTypeField(it, create(it.type))
                )
            }
            else {
                listOf(DefaultExpectedTypeField(it, create(it.type)))
            }
        }.flatten()
    }
}
