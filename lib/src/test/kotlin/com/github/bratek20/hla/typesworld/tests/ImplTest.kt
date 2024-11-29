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
        fun `should throw if same name type from different path already ensured`() {
            api.ensureType(worldType {
                name = "MyType"
                path = "MyPath"
            })

            assertApiExceptionThrown(
                { api.ensureType(worldType {
                    name = "MyType"
                    path = "OtherPath"
                }) },
                {
                    type = SameNameTypeExistsException::class
                    message = "Can not ensure 'OtherPath/MyType'. Type 'MyType' already exists for different path 'MyPath'"
                }
            )
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

        @Test
        fun `should get concrete List or Optional type with path of wrapped type`() {
            api.ensureType(worldType {
                name = "SomeClass"
                path = "SomePath"
            })

            api.getTypeByName(worldTypeName("List<SomeClass>")).let {
                assertWorldType(it) {
                    name = "List<SomeClass>"
                    path = "SomePath"
                }
            }

            api.getTypeByName(worldTypeName("Optional<SomeClass>")).let {
                assertWorldType(it) {
                    name = "Optional<SomeClass>"
                    path = "SomePath"
                }
            }
        }
    }

    @Nested
    inner class AddAndGetClassType {
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
        fun `should throw if type not found`() {
            assertApiExceptionThrown(
                { api.getTypeDependencies(worldType {
                    name = "NotExisting"
                    path = "SomePath"
                }) },
                {
                    type = WorldTypeNotFoundException::class
                    message = "Type 'SomePath/NotExisting' not found"
                }
            )
        }

        @Test
        fun `class type - extended class and fields are dependencies and are ensured`() {
            api.addClassType(worldClassType {
                type = {
                    name = "SomeClass"
                }
                extends = {
                    name = "SomeBaseClass"
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

            assertThat(dependencies).hasSize(2)
            assertWorldType(dependencies[0]) {
                name = "SomeBaseClass"
            }
            assertWorldType(dependencies[1]) {
                name = "OtherClass"
            }


            assertHasType(worldType {
                name = "SomeClass"
            })
            assertHasType(worldType {
                name = "SomeBaseClass"
            })
            assertHasType(worldType {
                name = "OtherClass"
            })
        }

        @Test
        fun `should get type dependencies and ensure types - concrete parametrized class type`() {
            api.addConcreteParametrizedClass(worldConcreteParametrizedClass {
                type = {
                    name = "SomeClass<OtherClass>"
                }
                typeArguments = listOf {
                    name = "OtherClass"
                }
            })

            val dependencies = api.getTypeDependencies(worldType {
                name = "SomeClass<OtherClass>"
            })

            assertThat(dependencies).hasSize(1)
            assertWorldType(dependencies[0]) {
                name = "OtherClass"
            }


            assertHasType(worldType {
                name = "SomeClass<OtherClass>"
            })
            assertHasType(worldType {
                name = "OtherClass"
            })
        }

        @Test
        fun `should get type dependencies and ensure types - concrete wrapper`() {
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

            assertHasType(worldType {
                name = "List<SomeClass>"
            })
            assertHasType(worldType {
                name = "SomeClass"
            })
        }

        @Test
        fun `dependencies of parametrized types and wrapped types are transitive`() {
            api.addConcreteParametrizedClass(worldConcreteParametrizedClass {
                type = {
                    name = "SomeClass<OtherClass>"
                }
                typeArguments = listOf {
                    name = "OtherClass"
                }
            })

            api.addConcreteWrapper(worldConcreteWrapper {
                type = {
                    name = "List<OtherClass2>"
                }
                wrappedType = {
                    name = "OtherClass2"
                }
            })

            api.addClassType(worldClassType {
                type = {
                    name = "SomeBaseClass"
                }
                extends = {
                    name = "SomeClass<OtherClass>"
                }
                fields = listOf {
                    name = "field"
                    type = {
                        name = "List<OtherClass2>"
                    }
                }
            })

            val dependencies = api.getTypeDependencies(worldType {
                name = "SomeBaseClass"
            })

            val dependenciesNames = dependencies.map { it.getName().value }
            assertThat(dependenciesNames).containsExactlyInAnyOrder(
                "SomeClass<OtherClass>",
                "OtherClass",
                "List<OtherClass2>",
                "OtherClass2"
            )
        }
    }

    fun assertHasType(type: WorldType) {
        assertThat(api.hasType(type))
            .withFailMessage {
                "Does not have ${type.getName()}"
            }
            .isTrue()
    }
}