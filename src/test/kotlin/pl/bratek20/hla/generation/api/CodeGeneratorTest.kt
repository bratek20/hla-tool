package pl.bratek20.hla.generation.api

import org.junit.jupiter.api.Test
import pl.bratek20.hla.directory.assertDirectory
import pl.bratek20.hla.generation.impl.CodeGeneratorLogic
import pl.bratek20.hla.model.hlaModule

class CodeGeneratorTest {
    private val codeGenerator = CodeGeneratorLogic()

    @Test
    fun `should generate code`() {
        val module = hlaModule {
            name = "SomeModule"
            valueObjects = listOf(
                {
                    name = "SomeId"
                    fields = listOf {
                        name = "value"
                        type = "string"
                    }
                },
                {
                    name = "SomeClass"
                    fields = listOf(
                        {
                            name = "id"
                            type = "SomeId"
                        },
                        {
                            name = "value"
                            type = "int"
                        }
                    )
                }
            )
        }

        val directory = codeGenerator.generateCode(module)

        assertDirectory(directory) {
            name = "SomeModule"
            files = listOf(
                {
                    name = "SomeId.java"
                },
                {
                    name = "SomeClass.java"
                }
            )
        }
    }
}