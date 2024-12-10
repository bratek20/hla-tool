package com.github.bratek20.hla.validations.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.context.stableContextBuilder
import com.github.bratek20.architecture.properties.PropertiesMock
import com.github.bratek20.architecture.properties.PropertiesMocks
import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.architecture.structs.api.Struct
import com.github.bratek20.architecture.structs.api.struct
import com.github.bratek20.hla.facade.HlaFacadeTest
import com.github.bratek20.hla.facade.api.HlaFacade
import com.github.bratek20.hla.facade.fixtures.profileName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesExtraInfo
import com.github.bratek20.hla.validations.api.*
import com.github.bratek20.hla.validations.context.ValidationsImpl
import com.github.bratek20.hla.validations.fixtures.assertValidationResult
import com.github.bratek20.logs.LoggerMock
import com.github.bratek20.logs.LogsMocks
import com.github.bratek20.utils.directory.fixtures.path
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

//SomePropertyEntry
//id: SomeId (idSource)
//
//SomeReferencingProperty
//referenceId: SomeId

//"SomeSourcePropertyList" -> SomePropertyEntry[]
//"SomeReferencingPropertyObject" -> SomeReferencingProperty
//"SomeReferencingPropertyList" -> SomeReferencingProperty[]


data class SomeId(
    val value: String
) {
    override fun toString(): String {
        return value.toString()
    }
}

data class SomeReferencingProperty(
    private val referenceId: String,
) {
    fun getReferenceId(): SomeId {
        return SomeId(this.referenceId)
    }

    companion object {
        fun create(
            referenceId: SomeId,
        ): SomeReferencingProperty {
            return SomeReferencingProperty(
                referenceId = referenceId.value,
            )
        }
    }
}

class SomeReferencingPropertyValidator: TypeValidator<SomeReferencingProperty> {
    override fun getType() = SomeReferencingProperty::class.java

    override fun validate(property: SomeReferencingProperty): ValidationResult {
        return ValidationResult.createFor(
            "Error for ${property.getReferenceId()}",
            "Other error for ${property.getReferenceId()}"
        )
    }
}

val SOME_SOURCE_PROPERTY_LIST_PROPERTY_KEY = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "SomeSourcePropertyList",
    Struct::class
)

val SOME_REFERENCING_PROPERTY_OBJECT_PROPERTY_KEY = com.github.bratek20.architecture.properties.api.ObjectPropertyKey(
    "SomeReferencingPropertyObject",
    Struct::class
)

val SOME_REFERENCING_PROPERTY_LIST_PROPERTY_KEY = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "SomeReferencingPropertyList",
    Struct::class
)

class ValidationsImplTest {
    private lateinit var validator: HlaValidator
    private lateinit var extraInfo: HlaTypesExtraInfo
    private lateinit var loggerMock: LoggerMock

    private lateinit var propertiesMock: PropertiesMock

    @BeforeEach
    fun beforeEach() {
        setup()
    }

    class SetupArgs(
        var typeValidatorsToInject: Class<out TypeValidator<*>>? = null
    )
    private fun setup(init: SetupArgs.() -> Unit = {}) {
        val args = SetupArgs().apply(init)

        val builder = stableContextBuilder()
            .withModules(
                LogsMocks(),
                ValidationsImpl()
            )

        args.typeValidatorsToInject?.let {
            builder.addImpl(TypeValidator::class.java, it)
        }

        val validationsContext = builder.build()

        validator = validationsContext.get(HlaValidator::class.java)
        extraInfo = validationsContext.get(HlaTypesExtraInfo::class.java)
        loggerMock = validationsContext.get(LoggerMock::class.java)

        propertiesMock = someContextBuilder()
            .withModules(
                PropertiesMocks()
            ).buildAndGet(PropertiesMock::class.java)
    }

    @Test
    fun `should pass + log info`() {
        propertiesMock.set(SOME_SOURCE_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "id" to "1"
            }
        ))

        propertiesMock.set(SOME_REFERENCING_PROPERTY_OBJECT_PROPERTY_KEY, struct {
            "referenceId" to "1"
        })

        propertiesMock.set(SOME_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "referenceId" to "1"
            }
        ))

        val result = validateCall()

        loggerMock.assertInfos(
            "Parsing module OtherModule",
            "Parsing module SimpleModule",
            "Parsing module SomeModule",
            "Parsing module TypesModule",

            "Source infos: [IdSourceInfo(type=WorldType(name=SomeId, path=SomeModule/Api/ValueObjects), fieldName=id, parent=WorldType(name=SomePropertyEntry, path=SomeModule/Api/ValueObjects))]",
            "Known properties: [otherProperty, otherProperties, SomeKey, SomeSourcePropertyList, SomeReferencingPropertyObject, SomeReferencingPropertyList]",

            "Allowed values for 'SomeId' from source '\"SomeSourcePropertyList\"/[*]/id': [1]",

            "Found reference for 'SomeId' at '\"SomeReferencingPropertyObject\"/referenceId'",
            "Values for '\"SomeReferencingPropertyObject\"/referenceId': [1]",

            "Found reference for 'SomeId' at '\"SomeReferencingPropertyList\"/[*]/referenceId'",
            "Values for '\"SomeReferencingPropertyList\"/[*]/referenceId': [1]"
        )

        assertValidationResult(result) {
            ok = true
        }
    }

    @Test
    fun `should fail if source ids not unique or other properties reference with wrong value`() {
        propertiesMock.set(SOME_SOURCE_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "id" to "1"
            },
            struct {
                "id" to "1"
            }
        ))

        propertiesMock.set(SOME_REFERENCING_PROPERTY_OBJECT_PROPERTY_KEY, struct {
            "referenceId" to "2"
        })

        propertiesMock.set(SOME_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "referenceId" to "3"
            }
        ))

        val result = validateCall()

        assertValidationResult(result) {
            ok = false
            errors = listOf(
                "Value '1' at '\"SomeSourcePropertyList\"/[*]/id' is not unique",
                "Value '2' at '\"SomeReferencingPropertyObject\"/referenceId' not found in source values from '\"SomeSourcePropertyList\"/[*]/id'",
                "Value '3' at '\"SomeReferencingPropertyList\"/[*]/referenceId' not found in source values from '\"SomeSourcePropertyList\"/[*]/id'",
            )
        }
    }

    @Test
    fun `should fail based on type validators`() {
        setup {
            typeValidatorsToInject = SomeReferencingPropertyValidator::class.java
        }

        propertiesMock.set(SOME_SOURCE_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "id" to "1"
            },
        ))

        propertiesMock.set(SOME_REFERENCING_PROPERTY_OBJECT_PROPERTY_KEY, struct {
            "referenceId" to "1"
        })

        propertiesMock.set(SOME_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "referenceId" to "1"
            }
        ))

        val result = validateCall()

        assertValidationResult(result) {
            ok = false
            errors = listOf(
                "Type validator failed at '\"SomeReferencingPropertyObject\"', message: Error for 1",
                "Type validator failed at '\"SomeReferencingPropertyObject\"', message: Other error for 1",
                "Type validator failed at '\"SomeReferencingPropertyList\"/[*]', message: Error for 1",
                "Type validator failed at '\"SomeReferencingPropertyList\"/[*]', message: Other error for 1",
            )
        }
    }

    private fun validateCall(): ValidationResult {
        return validator.validateProperties(
            path(HlaFacadeTest.HLA_FOLDER_PATH),
            profileName(HlaFacadeTest.KOTLIN_PROFILE),
            propertiesMock
        )
    }
}