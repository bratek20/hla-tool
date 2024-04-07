package pl.bratek20.hla.generation.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.assertDirectory
import pl.bratek20.hla.directory.impl.DirectoryLogic
import pl.bratek20.hla.generation.impl.CodeGeneratorLogic
import pl.bratek20.hla.model.hlaModule

class CodeGeneratorTest {
    private val codeGenerator = CodeGeneratorLogic()

    @Test
    fun `should generate code`() {
        val module = hlaModule {
            name = "SomeModule"
            simpleValueObjects = listOf {
                name = "SomeId"
                type = "String"
            }
        }

        val directory = codeGenerator.generateCode(module)

        assertDirectory(directory) {
            name = "somemodule"
            files = listOf {
                name = "SomeId.kt"
                content = listOf(
                    "package pl.bratek20.somemodule",
                    "",
                    "data class SomeId(",
                    "    val value: String",
                    ")"
                )
            }
        }
    }

    @Test
    fun `should generate code E2E`() {
        val module = hlaModule {
            name = "SomeModule"
            simpleValueObjects = listOf {
                name = "SomeId"
                type = "String"
            }
            complexValueObjects = listOf {
                name = "SomeClass"
                fields = listOf (
                    {
                        name = "id"
                        type = "SomeId"
                    },
                    {
                        name = "amount"
                        type = "Int"
                    }
                )
            }
            interfaces = listOf {
                name = "SomeInterface"
                methods = listOf (
                    {
                        name = "someCommand"
                        returnType = "Unit" //TODO: make it work for null
                        args = listOf (
                            {
                                name = "id"
                                type = "SomeId"
                            },
                            {
                                name = "amount"
                                type = "Int"
                            }
                        )
                    },
                    {
                        name = "someQuery"
                        returnType = "SomeClass"
                        args = listOf {
                            name = "id"
                            type = "SomeId"
                        }
                    }
                )
            }
        }

        val directory = codeGenerator.generateCode(module)

        val directoryApi = DirectoryLogic()
        val exampleDirectory = directoryApi.readDirectory(Path("example/src/main/java/pl/bratek20/somemodule"))

        val compareResult = directoryApi.compare(directory, exampleDirectory)
        assertThat(compareResult.same)
            .withFailMessage(compareResult.differences.toString())
            .isTrue()
    }
}