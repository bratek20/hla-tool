package com.github.bratek20.hla.types.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.types.api.TypesApi
import com.github.bratek20.hla.types.context.TypeImpl
import com.github.bratek20.hla.types.fixtures.assertHlaType
import com.github.bratek20.hla.types.fixtures.hlaType
import com.github.bratek20.hla.types.fixtures.structure
import com.github.bratek20.hla.types.fixtures.wrapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TypeImplTest {
    private lateinit var api: TypesApi

    @BeforeEach
    fun setUp() {
        api = someContextBuilder()
            .withModules(
                TypeImpl()
            )
            .get(TypesApi::class.java)
    }

    @Test
    fun `should work for structures`() {
        api.addStructure(structure {
            type = {
                name = "MyStructure"
            }
            fields = listOf {
                type = {
                    name = "MyFieldType"
                }
            }
        })

        api.getTypeDependencies(
            hlaType {
                name = "MyStructure"
            }
        ).let {
            assertThat(it).hasSize(1)
            assertHlaType(it[0]) {
                name = "MyFieldType"
            }
        }
    }

    @Test
    fun `should work for wrappers`() {
        api.addWrapper(wrapper {
            type = {
                name = "MyWrapper"
            }
            wrappedType = {
                name = "MyWrappedType"
            }
        })

        api.getTypeDependencies(
            hlaType {
                name = "MyWrapper"
            }
        ).let {
            assertThat(it).hasSize(1)
            assertHlaType(it[0]) {
                name = "MyWrappedType"
            }
        }
    }
}