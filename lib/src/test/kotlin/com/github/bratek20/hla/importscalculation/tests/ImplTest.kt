package com.github.bratek20.hla.importscalculation.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.facade.fixtures.moduleName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.fixtures.hlaTypePath
import com.github.bratek20.hla.importscalculation.api.ImportsCalculator
import com.github.bratek20.hla.importscalculation.context.ImportsCalculationImpl
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.context.TypesWorldImpl
import com.github.bratek20.hla.typesworld.fixtures.worldClassType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ImportsCalculationImplTest {
    private lateinit var calculator: ImportsCalculator
    private lateinit var typesWorldApi: TypesWorldApi

    @BeforeEach
    fun setUp() {
        val c = someContextBuilder()
            .withModules(
                TypesWorldImpl(),
                ImportsCalculationImpl()
            )
            .build()

        calculator = c.get(ImportsCalculator::class.java)
        typesWorldApi = c.get(TypesWorldApi::class.java)
    }

    @Test
    fun `should not duplicate same imports`() {
        typesWorldApi.addClassType(worldClassType {
            type = {
                name = "SomeType"
                path = "SomeModule/Api/ValueObjects"
            }
            fields = listOf (
                {
                    type = {
                        name = "OtherType"
                        path = "OtherModule/Api/ValueObjects"
                    }
                },
                {
                    type = {
                        name = "OtherType2"
                        path = "OtherModule/Api/ValueObjects"
                    }
                }
            )
        })

        testCalculate(
            "SomeModule/Api/ValueObjects",
            listOf(
                "OtherModule.Api"
            )
        )
    }

    @Test
    fun `should skip imports for paths starting with Language`() {
        typesWorldApi.addClassType(worldClassType {
            type = {
                name = "SomeType"
                path = "SomeModule/Api/ValueObjects"
            }
            fields = listOf {
                type = {
                    name = "int"
                    path = "Language/Types/Api/Primitives"
                }
            }
        })

        testCalculate(
            "SomeModule/Api/ValueObjects",
            emptyList()
        )
    }

    @Test
    fun `should skip import to current path`() {
        typesWorldApi.addClassType(worldClassType {
            type = {
                name = "SomeType"
                path = "SomeModule/Api/ValueObjects"
            }
            fields = listOf {
                type = {
                    name = "SomeType2"
                    path = "SomeModule/Api/ValueObjects"
                }
            }
        })

        testCalculate(
            "SomeModule/Api/ValueObjects",
            emptyList()
        )
    }

    @Test
    fun `should import extended class module`() {
        typesWorldApi.addClassType(worldClassType {
            type = {
                name = "SomeType"
                path = "SomeModule/Api/ValueObjects"
            }
            extends = {
                name = "SomeType2"
                path = "OtherModule/Api/ValueObjects"
            }
        })

        testCalculate(
            "SomeModule/Api/ValueObjects",
            listOf(
                "OtherModule.Api"
            )
        )
    }

    private fun testCalculate(
        path: String,
        expectedImports: List<String>
    ) {
        val imports = calculator.calculate(
            hlaTypePath(path)
        )

        assertThat(imports).containsExactlyElementsOf(expectedImports)
    }
}