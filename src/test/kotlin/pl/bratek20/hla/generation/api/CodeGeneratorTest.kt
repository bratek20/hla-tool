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
        }

        val directory = codeGenerator.generateCode(module)

        assertDirectory(directory) {
            name = "SomeModule"
        }
    }
}