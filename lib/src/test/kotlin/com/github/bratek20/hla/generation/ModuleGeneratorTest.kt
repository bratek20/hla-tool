package com.github.bratek20.hla.generation

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.exceptions.assertApiExceptionThrown
import com.github.bratek20.hla.generation.api.ModuleGenerator
import com.github.bratek20.hla.generation.api.UnknownTypeException
import com.github.bratek20.hla.generation.context.GenerationImpl
import com.github.bratek20.hla.generation.fixtures.generateArgs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ModuleGeneratorTest {
    private lateinit var moduleGenerator: ModuleGenerator
    @BeforeEach
    fun setUp() {
        moduleGenerator = someContextBuilder()
            .withModules(GenerationImpl())
            .get(ModuleGenerator::class.java)
    }

    //TODO to make it work, verification module is needed
    @Disabled
    @Test
    fun shouldThrowReadableExceptionWhenTypeIsUnknown() {
        //given
        val args = generateArgs {
            moduleToGenerate = "test"
            group = {
                modules = listOf {
                    name = "test"
                    complexValueObjects = listOf {
                        name = "SomeType"
                        fields = listOf {
                            name = "test"
                            type = {
                                name = "UnknownType"
                            }
                        }
                    }
                }
            }
        }

        // when
        assertApiExceptionThrown(
            { moduleGenerator.generate(args) },
            { type = UnknownTypeException::class }
        )
    }
}