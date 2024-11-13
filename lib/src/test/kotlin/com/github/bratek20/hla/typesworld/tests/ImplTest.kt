package com.github.bratek20.hla.typesworld.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.typesworld.api.*
import com.github.bratek20.hla.typesworld.context.TypesWorldImpl
import com.github.bratek20.hla.typesworld.fixtures.HlaTypeDef
import com.github.bratek20.hla.typesworld.fixtures.assertClassType
import com.github.bratek20.hla.typesworld.fixtures.classType
import com.github.bratek20.hla.typesworld.fixtures.hlaType
import org.junit.jupiter.api.Test

private val order0HlaType: HlaTypeDef.() -> Unit = {
    kind = "ClassType"
    name = "SomeType"
    path = "SomeRootGroup/SomeNestedGroup/SomeModule/Api/ValueObjects"
}

class Order0Populator : TypesWorldPopulator {
    override fun getOrder(): Int {
        return 0
    }

    override fun populate(api: TypesWorldApi) {
        api.addClassType(classType {
            type = order0HlaType
        })
    }
}

class Order1Populator : TypesWorldPopulator {
    override fun getOrder(): Int {
        return 1
    }

    override fun populate(api: TypesWorldApi) {
        val classType = api.getClassType(hlaType(order0HlaType))
        api.addClassType(ClassType.create(
            type = HlaType.create(
                kind = classType.getType().getKind(),
                name = HlaTypeName("PopulatedType"),
                path = classType.getType().getPath()
            ),
            fields = classType.getFields(),
        ))
    }
}

class TypesWorldImplTest {
    @Test
    fun `should work`() {
        val api = someContextBuilder()
            .withModule(TypesWorldImpl())
            .addImpl(TypesWorldPopulator::class.java, Order0Populator::class.java)
            .addImpl(TypesWorldPopulator::class.java, Order1Populator::class.java)
            .get(TypesWorldApi::class.java)

        val classType = api.getClassType(hlaType {
            kind = "ClassType"
            name = "PopulatedType"
            path = "SomeRootGroup/SomeNestedGroup/SomeModule/Api/ValueObjects"
        })

        assertClassType(classType) {
            type = {
                name = "PopulatedType"
            }
        }
    }
}