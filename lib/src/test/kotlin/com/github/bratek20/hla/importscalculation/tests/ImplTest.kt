package com.github.bratek20.hla.importscalculation.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.facade.fixtures.moduleName
import com.github.bratek20.hla.generation.api.SubmoduleName
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
    fun `should work`() {
        typesWorldApi.addClassType(worldClassType {
            type = {
                name = "SomeType"
                path = "SomeGroup/SomeModule/Api/ValueObjects"
            }
            fields = listOf {
                type = {
                    name = "OtherType"
                    path = "SomeGroup/OtherModule/Api/ValueObjects"
                }
            }
        })

        testCalculate(
            "SomeModule",
            SubmoduleName.Api,
            listOf(
                "SomeGroup.OtherModule.Api.ValueObjects"
            )
        )
    }

    private fun testCalculate(
        moduleName: String,
        submoduleName: SubmoduleName,
        expectedImports: List<String>
    ) {
        val imports = calculator.calculate(
            moduleName(moduleName),
            submoduleName
        )

        assertThat(imports).containsExactlyElementsOf(expectedImports)
    }
}