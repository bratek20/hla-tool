package com.github.bratek20.hla.validations.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.properties.PropertiesMock
import com.github.bratek20.architecture.properties.PropertiesMocks
import com.github.bratek20.architecture.properties.api.Properties
import com.github.bratek20.architecture.serialization.api.Struct
import com.github.bratek20.architecture.serialization.api.struct
import com.github.bratek20.hla.facade.HlaFacadeTest
import com.github.bratek20.hla.validations.api.HlaValidator
import com.github.bratek20.hla.validations.context.ValidationsImpl
import com.github.bratek20.hla.validations.fixtures.assertValidationResult
import com.github.bratek20.utils.directory.fixtures.path
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


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

//SomePropertyEntry
//id: SomeId (idSource)
//
//SomeReferencingProperty
//referenceId: SomeId

class ValidationsImplTest {
    private lateinit var validator: HlaValidator
    private lateinit var propertiesMock: PropertiesMock

    @BeforeEach
    fun setup() {
        validator = someContextBuilder()
            .withModules(
                ValidationsImpl()
            )
            .get(HlaValidator::class.java)

        propertiesMock = someContextBuilder()
            .withModules(
                PropertiesMocks()
            ).get(PropertiesMock::class.java)
    }

    @Test
    fun `should pass`() {
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

        val result = validator.validateProperties(
            path(HlaFacadeTest.HLA_FOLDER_PATH),
            propertiesMock
        )

        assertValidationResult(result) {
            ok = true
        }
    }

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

        val result = validator.validateProperties(
            path(HlaFacadeTest.HLA_FOLDER_PATH),
            propertiesMock
        )

        assertValidationResult(result) {
            ok = false
            errors = listOf(
                "Key 'SomeReferencingPropertyObject', path 'SomeReferencingProperty/referenceId' id '2' does not exist for key 'SomeSourcePropertyList' in 'SomePropertyEntry/id'",
                "Key 'SomeReferencingPropertyList', path 'SomeReferencingProperty/referenceId' id '3' does not exist for key 'SomeSourcePropertyList' in 'SomePropertyEntry/id'"
            )
        }
    }
}