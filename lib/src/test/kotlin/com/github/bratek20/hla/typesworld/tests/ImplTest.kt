package com.github.bratek20.hla.typesworld.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.exceptions.assertApiExceptionThrown
import com.github.bratek20.hla.typesworld.api.*
import com.github.bratek20.hla.typesworld.context.TypesWorldImpl
import com.github.bratek20.hla.typesworld.fixtures.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class Order0Populator : TypesWorldPopulator {
    override fun getOrder(): Int {
        return 0
    }

    override fun populate(api: TypesWorldApi) {
        api.addClassType(classType {
            type = {
                name = "SomeClass"
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

    @Test
    fun `should get class populated`() {
        val classType = api.getClassType(hlaType {
            name = "OtherClass"
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
            { api.getClassType(hlaType { name = "NotExisting" }) },
            {
                type = TypeNotFoundException::class
                message = "Class type 'NotExisting' not found"
            }
        )
    }

    @Test
    fun `should get type dependencies - class type`() {
        val type = hlaType {
            name = "OtherClass"
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