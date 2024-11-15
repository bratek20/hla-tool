package com.github.bratek20.hla.typesworld.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.exceptions.assertApiExceptionThrown
import com.github.bratek20.hla.typesworld.api.*
import com.github.bratek20.hla.typesworld.context.TypesWorldImpl
import com.github.bratek20.hla.typesworld.fixtures.*
import com.github.bratek20.utils.directory.fixtures.path
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Order0Populator : TypesWorldPopulator {
    override fun getOrder(): Int {
        return 0
    }

    override fun populate(api: TypesWorldApi) {
        api.addClassType(classType {
            type = {
                name = "SomeClass"
                path = "SomePath"
            }
        })
        api.addConcreteWrapper(concreteWrapper {
            type = {
                name = "List<SomeClass>"
            }
            wrappedType = {
                name = "SomeClass"
            }
        })
    }
}

class Order1Populator : TypesWorldPopulator {
    override fun getOrder(): Int {
        return 1
    }

    override fun populate(api: TypesWorldApi) {
        val classType = api.getClassType(hlaType {
            name = "SomeClass"
            path = "SomePath"
        })

        api.addClassType(ClassType.create(
            HlaType.create(
                name = HlaTypeName("OtherClass"),
                path = classType.getType().getPath(),
            ),
            fields = listOf(
                ClassField.create(
                    name = "someField",
                    type = classType.getType()
                )
            )
        ))
    }
}

class TypesWorldImplTest {
    private lateinit var api: TypesWorldApi

    @BeforeEach
    fun setUp() {
        api = someContextBuilder()
            .withModule(TypesWorldImpl())
            .addImpl(TypesWorldPopulator::class.java, Order0Populator::class.java)
            .addImpl(TypesWorldPopulator::class.java, Order1Populator::class.java)
            .get(TypesWorldApi::class.java)
    }

    @Nested
    inner class AddType {
        @Test
        fun `should add`() {
            api.addType(hlaType {
                name = "MyType"
                path = "MyPath"
            })

            api.hasType(hlaType {
                name = "MyType"
                path = "MyPath"
            }).let {
                assertThat(it).isTrue()
            }

            api.getTypeByName(hlaTypeName("MyType")).let {
                assertHlaType(it) {
                    path = "MyPath"
                }
            }
        }
    }
    @Nested
    inner class GetClassType {
        @Test
        fun `should get populated class`() {
            val classType = api.getClassType(hlaType {
                name = "OtherClass"
                path = "SomePath"
            })

            assertClassType(classType) {
                type = {
                    name = "OtherClass"
                }
            }
        }

        @Test
        fun `should throw exception when class not found`() {
            assertApiExceptionThrown(
                { api.getClassType(hlaType {
                    name = "NotExisting"
                    path = "SomePath"
                }) },
                {
                    type = TypeNotFoundException::class
                    message = "Class type 'SomePath/NotExisting' not found"
                }
            )
        }
    }

    @Nested
    inner class GetTypeDependencies {
        @Test
        fun `should get type dependencies - class type`() {
            val type = hlaType {
                name = "OtherClass"
                path = "SomePath"
            }

            val dependencies = api.getTypeDependencies(type)

            assertThat(dependencies).hasSize(1)
            assertHlaType(dependencies[0]) {
                name = "SomeClass"
            }
        }

        @Test
        fun `should get type dependencies - concrete wrapper`() {
            val type = hlaType {
                name = "List<SomeClass>"
            }

            val dependencies = api.getTypeDependencies(type)

            assertThat(dependencies).hasSize(1)
            assertHlaType(dependencies[0]) {
                name = "SomeClass"
            }
        }
    }

    @Nested
    inner class GetTypeByName {
        @Test
        fun `should get populated type by name`() {
            api.getTypeByName(hlaTypeName("SomeClass")).let {
                assertHlaType(it) {
                    name = "SomeClass"
                    path = "SomePath"
                }
            }
        }

        @Test
        fun `should throw exception when type not found`() {
            assertApiExceptionThrown(
                { api.getTypeByName(hlaTypeName("NotExisting")) },
                {
                    type = TypeNotFoundException::class
                    message = "Hla type with name 'NotExisting' not found"
                }
            )
        }
    }
}