package com.github.bratek20.hla.mvvmtypesmappers.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.mvvmtypesmappers.api.ViewModelToViewMapper
import com.github.bratek20.hla.mvvmtypesmappers.context.MvvmTypesMappersImpl
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.context.TypesWorldImpl
import com.github.bratek20.hla.typesworld.fixtures.assertWorldType
import com.github.bratek20.hla.typesworld.fixtures.worldClassType
import com.github.bratek20.hla.typesworld.fixtures.worldConcreteParametrizedClass
import com.github.bratek20.hla.typesworld.fixtures.worldType
import com.github.bratek20.hla.typesworld.tests.TypesWorldImplTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MvvmTypesMappersImplTest {
    @Test
    fun `should skip Vm suffix of ViewModel for View name`() {
        val c = someContextBuilder()
            .withModules(
                TypesWorldImpl(),
                MvvmTypesMappersImpl()
            )
            .build()

        val typesWorldApi = c.get(TypesWorldApi::class.java)
        val mapper = c.get(ViewModelToViewMapper::class.java)

        typesWorldApi.addConcreteParametrizedClass(
            worldConcreteParametrizedClass {
                type = {
                    name = "UiElement<SomeModel>"
                }
                typeArguments = listOf {
                    name = "SomeModel"
                }
            }
        )
        typesWorldApi.addClassType(worldClassType {
            type = {
                name = "SomeVm"
            }
            extends = {
                name = "UiElement<SomeModel>"
            }
        })

        val type = mapper.map(worldType {
            name = "SomeVm"
        })

        assertWorldType(type) {
            name = "SomeView"
        }
    }
}