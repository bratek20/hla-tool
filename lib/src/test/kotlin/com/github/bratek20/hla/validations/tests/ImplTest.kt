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
import com.github.bratek20.utils.directory.fixtures.path
import org.assertj.core.api.Assertions.assertThat
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
    @Test
    fun `should pass`() {
        val validator = someContextBuilder()
            .withModules(
                ValidationsImpl()
            )
            .get(HlaValidator::class.java)

        val propertiesMock = someContextBuilder()
            .withModules(
                PropertiesMocks()
            ).get(PropertiesMock::class.java)

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

        validator.validateProperties(
            path(HlaFacadeTest.HLA_FOLDER_PATH),
            propertiesMock
        )
    }
}