package com.github.bratek20.hla.validations.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.context.stableContextBuilder
import com.github.bratek20.architecture.properties.PropertiesMock
import com.github.bratek20.architecture.properties.PropertiesMocks
import com.github.bratek20.architecture.structs.api.Struct
import com.github.bratek20.architecture.structs.api.struct
import com.github.bratek20.architecture.structs.api.structList
import com.github.bratek20.hla.definitions.api.KeyDefinition
import com.github.bratek20.hla.definitions.fixtures.keyDefinition
import com.github.bratek20.hla.facade.HlaFacadeTest
import com.github.bratek20.hla.facade.fixtures.profileName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesExtraInfo
import com.github.bratek20.hla.validations.api.*
import com.github.bratek20.hla.validations.context.ValidationsImpl
import com.github.bratek20.hla.validations.fixtures.assertValidationResult
import com.github.bratek20.logs.LoggerMock
import com.github.bratek20.logs.LogsMocks
import com.github.bratek20.utils.directory.fixtures.path
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

//SomePropertyEntry
//id: SomeId (idSource)
//
//SomeReferencingProperty
//referenceId: SomeId

//"SomeSourcePropertyList" -> SomePropertyEntry[]
//"SomeReferencingPropertyObject" -> SomeReferencingProperty
//"SomeReferencingPropertyList" -> SomeReferencingProperty[]

//    NestedValue
//        value: string
//    OptionalFieldProperty
//        optionalField: NestedValue?

//    "OptionalFieldProperties" -> OptionalFieldProperty[]

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

data class NestedValue(
    private val value: String,
) {
    fun getValue(): String {
        return this.value
    }

    companion object {
        fun create(
            value: String,
        ): NestedValue {
            return NestedValue(
                value = value,
            )
        }
    }
}

class SomeReferencingPropertyFailingValidator: TypeValidator<SomeReferencingProperty> {
    override fun validate(property: SomeReferencingProperty, c: ValidationContext): ValidationResult {
        return ValidationResult.createFor(
            "Error for ${property.getReferenceId()}",
            "Other error for ${property.getReferenceId()}"
        )
    }
}

class SomeReferencingPropertyOkValidator: TypeValidator<SomeReferencingProperty> {
    override fun validate(property: SomeReferencingProperty, c: ValidationContext): ValidationResult {
        return ValidationResult.ok()
    }
}

class SomeIdFailValidator: TypeValidator<SomeId> {
    override fun validate(property: SomeId, c: ValidationContext): ValidationResult {
        return ValidationResult.createFor(
            "Error for ${property.value} at ${c.getPath()}"
        )
    }
}

class SomeIdOkValidator: TypeValidator<SomeId> {
    override fun validate(property: SomeId, c: ValidationContext): ValidationResult {
        return ValidationResult.ok()
    }
}

class NestedValueValidator: TypeValidator<NestedValue> {
    override fun validate(property: NestedValue, c: ValidationContext): ValidationResult {
        return ValidationResult.ok()
    }
}

class DateOkValidator: SimpleCustomTypeValidator<Date, String> {
    override fun createFunction(): (value: String) -> Date = ::dateCreate

    override fun validate(property: Date, c: ValidationContext): ValidationResult {
        return ValidationResult.ok()
    }
}

class DateRangeOkValidator: ComplexCustomTypeValidator<DateRange, SerializedDateRange> {

    override fun validate(property: DateRange, c: ValidationContext): ValidationResult {
        return ValidationResult.ok()
    }
}

class DateFailValidator: SimpleCustomTypeValidator<Date, String> {
    override fun createFunction(): (value: String) -> Date = ::dateCreate

    override fun validate(property: Date, c: ValidationContext): ValidationResult {
        return ValidationResult.createFor(
            "Error for ${property.value2}"
        )
    }
}

class DateRangeFailValidator: ComplexCustomTypeValidator<DateRange, SerializedDateRange> {

    override fun validate(property: DateRange, c: ValidationContext): ValidationResult {
        return ValidationResult.createFor(
            "Error for ${property.from.value2}, ${property.to.value2}"
        )
    }
}

val SOME_SOURCE_PROPERTY_LIST_PROPERTY_KEY = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "SomeSourcePropertyList",
    Struct::class
)

val SOME_RENAMED_SOURCE_PROPERTY_LIST_PROPERTY_KEY = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "SomeRenamedSourcePropertyEntryList",
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

val SOME_RENAMED_REFERENCING_PROPERTY_LIST_PROPERTY_KEY = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "SomeRenamedReferencingPropertyList",
    Struct::class
)

val SOME_RENAMED_REFERENCING_RENAMED_PROPERTY_LIST_PROPERTY_KEY = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "SomeRenamedReferencingRenamedPropertyList",
    Struct::class
)

val SOME_REFERENCING_PROPERTY_FIELD_LIST_PROPERTY_KEY = com.github.bratek20.architecture.properties.api.ObjectPropertyKey(
    "SomeReferencingPropertyFieldList",
    Struct::class
)

val SOME_STRUCTURE_WITH_UNIQUE_IDS_LIST = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "SomeStructureWithUniqueIdsList",
    Struct::class
)

val SOME_STRUCTURE_WITH_UNIQUE_IDS_OBJECT = com.github.bratek20.architecture.properties.api.ObjectPropertyKey(
    "SomeStructureWithUniqueIdsObject",
    Struct::class
)


val SOME_STRUCTURE_WITH_UNIQUE_NESTED_IDS = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "SomeStructureWithUniqueNestedIds",
    Struct::class
)
val SOME_STRUCTURE_WITH_UNIQUE_IDS_MULTIPLE_NEST = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "SomeStructureWithUniqueIdsMultipleNest",
    Struct::class
)

val CUSTOM_TYPES_PROPERTY_PROPERTY_KEY = com.github.bratek20.architecture.properties.api.ObjectPropertyKey(
    "CustomTypesProperty",
    Struct::class
)

val SomeStructWithNestedOtherClassUniqueIds = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "SomeStructWithNestedOtherClassUniqueIds",
    Struct::class
)

val COMPLEX_SRUCTURE_WITH_NESTED_UNIQUE_IDS = com.github.bratek20.architecture.properties.api.ListPropertyKey(
    "ComplexStructureWithNestedUniqueIds",
    Struct::class
)


class ValidationsImplTest {
    private lateinit var validator: HlaValidator
    private lateinit var extraInfo: HlaTypesExtraInfo
    private lateinit var loggerMock: LoggerMock

    private lateinit var propertiesMock: PropertiesMock

    class SetupArgs(
        var typeValidatorsToInject: List<Class<out TypeValidator<*>>>? = null
    )
    private fun setup(init: SetupArgs.() -> Unit = {}) {
        val args = SetupArgs().apply(init)

        val builder = stableContextBuilder()
            .withModules(
                LogsMocks(),
                ValidationsImpl()
            )

        args.typeValidatorsToInject?.forEach {
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
        setup {
            typeValidatorsToInject = listOf(
                SomeReferencingPropertyOkValidator::class.java,
                SomeIdOkValidator::class.java
            )
        }

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

        propertiesMock.set(SOME_REFERENCING_PROPERTY_FIELD_LIST_PROPERTY_KEY,
            struct {
                "referenceIdList" to listOf("1")
            }
        )

        propertiesMock.set(SOME_RENAMED_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "rId" to "1"
            }
        ))

        propertiesMock.set(SomeStructWithNestedOtherClassUniqueIds, listOf(
            struct {
                "someNestedWithUniqueIds" to structList(
                    {
                        "otherClass" to struct {
                            "uniqueId" to "1"
                        }
                    },
                    {
                        "otherClass" to struct {
                            "uniqueId" to "2"
                        }
                    }
                )
            }
        ))

        propertiesMock.set(COMPLEX_SRUCTURE_WITH_NESTED_UNIQUE_IDS, listOf(
            struct {
                "id" to "1"
                "nestLevel1" to structList(
                    {
                        "nestLevel2" to structList(
                            {
                                "uniqueIds" to structList(
                                    {
                                        "uniqueId" to "2"
                                    },
                                    {
                                        "uniqueId" to "3"
                                    }
                                )
                            }
                        )
                    }
                )
            },
            struct {
                "id" to "2"
                "nestLevel1" to structList(
                    {
                        "nestLevel2" to structList(
                            {
                                "uniqueIds" to structList(
                                    {
                                        "uniqueId" to "2"
                                    },
                                    {
                                        "uniqueId" to "3"
                                    }
                                )
                            }
                        )
                    }
                )
            }
        ))


        val result = validateCall()

        loggerMock.assertInfos(
            "Parsing group hla",
            "Parsing module NoInterfacesModule",
            "Parsing module OtherModule",
            "Parsing module SimpleModule",
            "Parsing module SomeModule",
            "Parsing module TypesModule",

            "Source infos: [IdSourceInfo(type=WorldType(name=SomeId, path=SomeModule/Api/ValueObjects), fieldName=id, parent=WorldType(name=SomePropertyEntry, path=SomeModule/Api/ValueObjects))]",

            "Allowed values for 'SomeId' from source '\"SomeSourcePropertyList\"/[*]/id': [1]",
            "Found reference for 'SomeId' at '\"SomeReferencingPropertyObject\"/referenceId'",
            "Values for '\"SomeReferencingPropertyObject\"/referenceId': [1]",
            "Found reference for 'SomeId' at '\"SomeReferencingPropertyList\"/[*]/referenceId'",
            "Values for '\"SomeReferencingPropertyList\"/[*]/referenceId': [1]",
            "Found reference for 'SomeId' at '\"SomeRenamedReferencingPropertyList\"/[*]/rId'",
            "Values for '\"SomeRenamedReferencingPropertyList\"/[*]/rId': [1]",
            "Found reference for 'SomeId' at '\"SomeReferencingPropertyFieldList\"/referenceIdList/[*]'",
            "Values for '\"SomeReferencingPropertyFieldList\"/referenceIdList/[*]': [1]",


            "Validating type 'SomeReferencingProperty'",

            "Found reference for 'SomeReferencingProperty' at '\"SomeReferencingPropertyObject\"/'",
            "Found reference for 'SomeReferencingProperty' at '\"SomeReferencingPropertyList\"/[*]'",

            "Validating type 'SomeId'",
            "Found reference for 'SomeId' at '\"SomeSourcePropertyList\"/[*]/id'",
            "Found reference for 'SomeId' at '\"SomeReferencingPropertyObject\"/referenceId'",
            "Found reference for 'SomeId' at '\"SomeReferencingPropertyList\"/[*]/referenceId'",
            "Found reference for 'SomeId' at '\"SomeRenamedReferencingPropertyList\"/[*]/rId'",
            "Found reference for 'SomeId' at '\"SomeReferencingPropertyFieldList\"/referenceIdList/[*]'",
            "Unique id infos: [UniqueIdInfo(type=WorldType(name=string, path=Language/Types/Api/Primitives), fieldName=uniqueId, parent=WorldType(name=OtherClassWIthUniqueId, path=OtherModule/Api/ValueObjects)), UniqueIdInfo(type=WorldType(name=string, path=Language/Types/Api/Primitives), fieldName=id, parent=WorldType(name=UniqueIdEntry, path=SimpleModule/Api/ValueObjects))]"
        )


        assertValidationResult(result) {
            ok = true
        }
    }

    @Test
    fun `should pass unique validation for complex structures` () {
        setup {
            typeValidatorsToInject = listOf(
                SomeReferencingPropertyOkValidator::class.java,
                SomeIdOkValidator::class.java
            )
        }
        propertiesMock.set(COMPLEX_SRUCTURE_WITH_NESTED_UNIQUE_IDS, listOf(
            struct {
                "id" to "1"
                "nestLevel1" to structList(
                    {
                        "nestLevel2" to structList(
                            {
                                "uniqueIds" to structList(
                                    {
                                        "uniqueId" to "2"
                                    },
                                    {
                                        "uniqueId" to "3"
                                    }
                                )
                            }
                        )
                    }
                )
            },
            struct {
                "id" to "2"
                "nestLevel1" to emptyList<Struct>()
            },
            struct {
                "id" to "3"
                "nestLevel1" to structList(
                    {
                        "nestLevel2" to emptyList<Struct>()
                    }
                )
            },
            struct {
                "id" to "1"
                "nestLevel1" to structList(
                    {
                        "nestLevel2" to structList(
                            {
                                "uniqueIds" to emptyList<Struct>()
                            }
                        )
                    }
                )
            }
        ))

        val result = validateCall()

        assertValidationResult(result) {
            ok = true
        }
    }

    @Test
    fun `should fail if source ids not unique or other properties reference with wrong value`() {
        setup()

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
                "referenceId" to "1"
            },
            struct {
                "referenceId" to "3"
            }
        ))

        propertiesMock.set(SOME_REFERENCING_PROPERTY_FIELD_LIST_PROPERTY_KEY,
            struct {
                "referenceIdList" to listOf("4")
            }
        )

        val result = validateCall()

        assertValidationResult(result) {
            ok = false
            errors = listOf(
                "Value '1' at '\"SomeSourcePropertyList\"/[*]/id' is not unique",
                "Value '2' at '\"SomeReferencingPropertyObject\"/referenceId' not found in source values from '\"SomeSourcePropertyList\"/[*]/id'",
                "Value '3' at '\"SomeReferencingPropertyList\"/[1]/referenceId' not found in source values from '\"SomeSourcePropertyList\"/[*]/id'",
                "Value '4' at '\"SomeReferencingPropertyFieldList\"/referenceIdList/[0]' not found in source values from '\"SomeSourcePropertyList\"/[*]/id'"
            )
        }
    }

    @Test
    fun `should validate referred renamed property`() {
        setup()

        propertiesMock.set(SOME_SOURCE_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "id" to "1"
            }
        ))

        propertiesMock.set(SOME_RENAMED_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "rId" to "1"
            },
            struct {
                "rId" to "3"
            }
        ))

        val result = validateCall()

        assertValidationResult(result) {
            ok = false
            errors = listOf(
                "Value '3' at '\"SomeRenamedReferencingPropertyList\"/[1]/rId' not found in source values from '\"SomeSourcePropertyList\"/[*]/id'",
            )
        }
    }

    @Test
    fun `should validate referred renamed source`() {
        setup()

        propertiesMock.set(
            SOME_RENAMED_SOURCE_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "sId" to "1"
            }
        ))

        propertiesMock.set(SOME_RENAMED_REFERENCING_RENAMED_PROPERTY_LIST_PROPERTY_KEY, listOf(
            struct {
                "rId" to "1"
            },
            struct {
                "rId" to "3"
            }
        ))

        val result = validateCall()

        assertValidationResult(result) {
            ok = false
            errors = listOf(
                "Value '3' at '\"SomeRenamedReferencingRenamedPropertyList\"/[1]/rId' not found in source values from '\"SomeRenamedSourcePropertyEntryList\"/[*]/sId'",
            )
        }
    }

    @Test
    fun `should fail if unique id in structures is not unique`() {
        setup()

        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_IDS_LIST, listOf(
                struct {
                    "entries" to listOf(
                        struct {
                            "id" to "1"
                        },
                        struct {
                            "id" to "1"
                        }
                    )
                }
            )
        )

        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_IDS_OBJECT,
            struct {
                "entries" to listOf(
                    struct {
                        "id" to "1"
                    },
                    struct {
                        "id" to "1"
                    }
                )
            }
        )

        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_NESTED_IDS, listOf(
                struct {
                    "nestedUniqueIds" to listOf(
                        struct {
                            "entries" to listOf(
                                struct {
                                    "id" to "1"
                                },
                                struct {
                                    "id" to "1"
                                }
                            )
                        },
                        struct {
                            "entries" to listOf(
                                struct {
                                    "id" to "2"
                                },
                                struct {
                                    "id" to "2"
                                }
                            )
                        }
                    )
                }
            )
        )

        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_IDS_MULTIPLE_NEST, listOf(
                struct {
                    "moreNestedFields" to listOf(
                        struct {
                            "nestedUniqueIds" to listOf(
                                struct {
                                    "entries" to listOf(
                                        struct {
                                            "id" to "1"
                                        },
                                        struct {
                                            "id" to "1"
                                        }
                                    )
                                },
                                struct {
                                    "entries" to listOf(
                                        struct {
                                            "id" to "2"
                                        },
                                        struct {
                                            "id" to "2"
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        )


        val result = validateCall()

        assertValidationResult(result) {
            ok = false
            errors = listOf(
                "Value '1' at '\"SomeStructureWithUniqueIdsList\"/[0]/entries/[*]/id' is not unique",
                "Value '1' at '\"SomeStructureWithUniqueNestedIds\"/[0]/nestedUniqueIds/[0]/entries/[*]/id' is not unique",
                "Value '2' at '\"SomeStructureWithUniqueNestedIds\"/[0]/nestedUniqueIds/[1]/entries/[*]/id' is not unique",
                "Value '1' at '\"SomeStructureWithUniqueIdsObject\"/entries/[*]/id' is not unique",
                "Value '1' at '\"SomeStructureWithUniqueIdsMultipleNest\"/[0]/moreNestedFields/[0]/nestedUniqueIds/[0]/entries/[*]/id' is not unique",
                "Value '2' at '\"SomeStructureWithUniqueIdsMultipleNest\"/[0]/moreNestedFields/[0]/nestedUniqueIds/[1]/entries/[*]/id' is not unique"

            )
        }
    }

    @Test
    fun `should not fail if unique id in structures is empty`() {
        setup()

        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_IDS_LIST, listOf()
        )

        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_NESTED_IDS, emptyList<Struct>()
        )

        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_IDS_MULTIPLE_NEST, emptyList<Struct>()
        )

        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_IDS_OBJECT,
            struct {
                "entries" to emptyList<Struct>()
            }
        )

        val result = validateCall()

        assertValidationResult(result) {
            ok = true
        }
    }

    @Test
    fun `should not fail if unique is correct`() {
        setup()

        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_IDS_LIST, listOf(
                struct {
                    "entries" to listOf(
                        struct {
                            "id" to "1"
                        },
                        struct {
                            "id" to "2"
                        }
                    )
                },
                struct {
                    "entries" to listOf(
                        struct {
                            "id" to "1"
                        },
                        struct {
                            "id" to "2"
                        }
                    )
                }
            )
        )
        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_NESTED_IDS, listOf(
                struct {
                    "nestedUniqueIds" to listOf(
                        struct {
                            "entries" to listOf(
                                struct {
                                    "id" to "1"
                                },
                                struct {
                                    "id" to "2"
                                }
                            )
                        },
                        struct {
                            "entries" to listOf(
                                struct {
                                    "id" to "1"
                                },
                                struct {
                                    "id" to "2"
                                }
                            )
                        }
                    )
                }
            )
        )

        propertiesMock.set(
            SOME_STRUCTURE_WITH_UNIQUE_IDS_OBJECT,
            struct {
                "entries" to listOf(
                    struct {
                        "id" to "1"
                    },
                    struct {
                        "id" to "2"
                    }
                )
            }
        )


        val result = validateCall()

        assertValidationResult(result) {
            ok = true
        }
    }

    @Test
    fun `should fail based on type validators`() {
        setup {
            typeValidatorsToInject = listOf(
                SomeReferencingPropertyFailingValidator::class.java,
                SomeIdFailValidator::class.java
            )
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
                "Type validator failed at '\"SomeReferencingPropertyObject\"/', message: Error for 1",
                "Type validator failed at '\"SomeReferencingPropertyObject\"/', message: Other error for 1",
                "Type validator failed at '\"SomeReferencingPropertyList\"/[0]', message: Error for 1",
                "Type validator failed at '\"SomeReferencingPropertyList\"/[0]', message: Other error for 1",

                """Type validator failed at '"SomeSourcePropertyList"/[0]/id', message: Error for 1 at "SomeSourcePropertyList"/[0]/id""",
                """Type validator failed at '"SomeReferencingPropertyObject"/referenceId', message: Error for 1 at "SomeReferencingPropertyObject"/referenceId""",
                """Type validator failed at '"SomeReferencingPropertyList"/[0]/referenceId', message: Error for 1 at "SomeReferencingPropertyList"/[0]/referenceId""",
            )
        }
    }

    @Test
    fun `should traverse optional fields without any errors`() {
        setup {
            typeValidatorsToInject = listOf(
                NestedValueValidator::class.java
            )
        }

        propertiesMock.set(com.github.bratek20.architecture.properties.api.ListPropertyKey(
            "OptionalFieldProperties",
            Struct::class
        ), listOf(
            struct {
                "optionalField" to null
            },
            struct {
                "optionalField" to struct {
                    "value" to "some value"
                }
            }
        ))

        val result = validateCall()

        assertValidationResult(result) {
            ok = true
        }
    }

//    fun getListSizeAt(path: PropertyValuePathLogic): Int {
//        return 0
//    }
//
//    @Test
//    fun `XXX` () {
//        expandPathExceptLastListEntry("", keyDefinition{name = ""}, "", this::getListSizeAt)
//    }


    @Nested
    inner class CustomTypesScope {
        @Test
        fun `should pass custom types validation`() {
            setup {
                typeValidatorsToInject = listOf(
                    DateOkValidator::class.java,
                    DateRangeOkValidator::class.java
                )
            }

            propertiesMock.set(CUSTOM_TYPES_PROPERTY_PROPERTY_KEY, struct {
                "date" to "2021-01-01"
                "dateRange" to struct {
                    "from" to "2021-01-01"
                    "to" to "2021-01-01"
                }
            })

            val result = validateCall()

            assertValidationResult(result) {
                ok = true
            }
        }

        @Test
        fun `should fail custom types validation`() {
            setup {
                typeValidatorsToInject = listOf(
                    DateFailValidator::class.java,
                    DateRangeFailValidator::class.java
                )
            }

            propertiesMock.set(CUSTOM_TYPES_PROPERTY_PROPERTY_KEY, struct {
                "date" to "2021-01-02"
                "dateRange" to struct {
                    "from" to "2021-01-03"
                    "to" to "2021-01-04"
                }
            })

            val result = validateCall()

            assertValidationResult(result) {
                errors = listOf(
                    "Type validator failed at '\"CustomTypesProperty\"/date', message: Error for 2021-01-02",
                    "Type validator failed at '\"CustomTypesProperty\"/dateRange/from', message: Error for 2021-01-03",
                    "Type validator failed at '\"CustomTypesProperty\"/dateRange/to', message: Error for 2021-01-04",
                    "Type validator failed at '\"CustomTypesProperty\"/dateRange', message: Error for 2021-01-03, 2021-01-04"
                )
            }
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