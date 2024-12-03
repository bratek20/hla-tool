package com.github.bratek20.hla.validations.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.properties.PropertiesMock
import com.github.bratek20.architecture.properties.PropertiesMocks
import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.architecture.structs.api.Struct
import com.github.bratek20.architecture.structs.api.struct
import com.github.bratek20.hla.facade.HlaFacadeTest
import com.github.bratek20.hla.facade.api.HlaFacade
import com.github.bratek20.hla.facade.fixtures.profileName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesExtraInfo
import com.github.bratek20.hla.validations.api.HlaValidator
import com.github.bratek20.hla.validations.api.ValidationResult
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
    fun setup() {
        val validationsContext = someContextBuilder()
            .withModules(
                LogsMocks(),
                ValidationsImpl()
            )
            .build()

        validator = validationsContext.get(HlaValidator::class.java)
        extraInfo = validationsContext.get(HlaTypesExtraInfo::class.java)
        loggerMock = validationsContext.get(LoggerMock::class.java)

        propertiesMock = someContextBuilder()
            .withModules(
                PropertiesMocks()
            ).get(PropertiesMock::class.java)
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
            "Allowed values for 'SomeId' from source '\"SomeSourcePropertyList\"/[*]/id': [1]",

            "Checking properties: [otherProperty, otherProperties, SomeKey, SomeSourcePropertyList, SomeReferencingPropertyObject, SomeReferencingPropertyList]",

            "Found reference for 'SomeId' at '\"SomeReferencingPropertyObject\"/referenceId'",
            "Found reference for 'SomeId' at '\"SomeReferencingPropertyList\"/[*]/referenceId'",
        )

        assertValidationResult(result) {
            ok = true
        }
    }

    @Disabled
    @Test
    fun `should fail`() {
        propertiesMock.set(SOME_SOURCE_PROPERTY_LIST_PROPERTY_KEY, listOf(
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
                "Value '2' at '\"SomeReferencingPropertyObject\"/referenceId' not found in source values from '\"SomeSourcePropertyList\"/[*]/id'",
                "Value '3' at '\"SomeReferencingPropertyList\"/[0]/referenceId' not found in source values from '\"SomeSourcePropertyList\"/[*]/id'",
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