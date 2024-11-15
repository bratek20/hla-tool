package com.github.bratek20.hla.typesworld.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.exceptions.assertApiExceptionThrown
import com.github.bratek20.hla.typesworld.api.*
import com.github.bratek20.hla.typesworld.context.TypesWorldImpl
import com.github.bratek20.hla.typesworld.fixtures.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TypesWorldImplTest {
    private lateinit var api: TypesWorldApi

    @BeforeEach
    fun setUp() {
        api = someContextBuilder()
            .withModule(TypesWorldImpl())
            .get(TypesWorldApi::class.java)
    }

    @Nested
    inner class AddTypeAndGetTypeByName {
        @Test
        fun `should add`() {
            api.ensureType(worldType {
                name = "MyType"
                path = "MyPath"
            })

            api.hasType(worldType {
                name = "MyType"
                path = "MyPath"
            }).let {
                assertThat(it).isTrue()
            }

            api.getTypeByName(worldTypeName("MyType")).let {
                assertWorldType(it) {
                    path = "MyPath"
                }
            }
        }

        @Test
        fun `should get populated type by name`() {
            api.ensureType(worldType {
                name = "SomeClass"
                path = "SomePath"
            })

            api.getTypeByName(worldTypeName("SomeClass")).let {
                assertWorldType(it) {
                    name = "SomeClass"
                    path = "SomePath"
                }
            }
        }

        @Test
        fun `should throw exception when type not found`() {
            assertApiExceptionThrown(
                { api.getTypeByName(worldTypeName("NotExisting")) },
                {
                    type = WorldTypeNotFoundException::class
                    message = "Hla type with name 'NotExisting' not found"
                }
            )
        }
    }

    @Nested
    inner class GetClassType {
        @Test
        fun `should get populated class`() {
            api.addClassType(worldClassType {
                type = {
                    name = "SomeClass"
                }
            })

            val worldClassType = api.getClassType(worldType {
                name = "SomeClass"
            })

            assertWorldClassType(worldClassType) {
                type = {
                    name = "SomeClass"
                }
            }
        }

        @Test
        fun `should throw exception when class not found`() {
            assertApiExceptionThrown(
                { api.getClassType(worldType {
                    name = "NotExisting"
                    path = "SomePath"
                }) },
                {
                    type = WorldTypeNotFoundException::class
                    message = "Class type 'SomePath/NotExisting' not found"
                }
            )
        }
    }

    @Nested
    inner class GetTypeDependencies {
        @Test
        fun `should get type dependencies - class type`() {
            api.addClassType(worldClassType {
                type = {
                    name = "SomeClass"
                }
                fields = listOf {
                    name = "field"
                    type = {
                        name = "OtherClass"
                    }
                }
            })

            val dependencies = api.getTypeDependencies(worldType {
                name = "SomeClass"
            })

            assertThat(dependencies).hasSize(1)
            assertWorldType(dependencies[0]) {
                name = "OtherClass"
            }
        }

        @Test
        fun `should get type dependencies - concrete wrapper`() {
            api.addConcreteWrapper(worldConcreteWrapper {
                type = {
                    name = "List<SomeClass>"
                }
                wrappedType = {
                    name = "SomeClass"
                }
            })

            val dependencies = api.getTypeDependencies(worldType {
                name = "List<SomeClass>"
            })

            assertThat(dependencies).hasSize(1)
            assertWorldType(dependencies[0]) {
                name = "SomeClass"
            }
        }
    }
}