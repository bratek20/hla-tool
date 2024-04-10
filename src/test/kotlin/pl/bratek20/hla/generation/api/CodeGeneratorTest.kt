package pl.bratek20.hla.generation.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoryLogic
import pl.bratek20.hla.generation.impl.CodeGeneratorImpl
import pl.bratek20.hla.model.hlaModule
import java.util.stream.Stream

class CodeGeneratorTest {
    private val codeGenerator = CodeGeneratorImpl()

    fun module() = hlaModule {
        name = "SomeModule"
        simpleValueObjects = listOf {
            name = "SomeId"
            type = "String"
        }
        complexValueObjects = listOf(
            {
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
            },
            {
                name = "SomeClass2"
                fields = listOf (
                    {
                        name = "id"
                        type = "SomeId"
                    },
                    {
                        name = "enabled"
                        type = "Boolean"
                    }
                )
            }
        )
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

    class MyArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> =
            Stream.of(
                Arguments.of(
                    "example/kotlin/src/main/java/pl/bratek20/somemodule",
                    GeneratorLanguage.KOTLIN
                ),
                Arguments.of(
                    "example/typescript/SomeModule",
                    GeneratorLanguage.TYPE_SCRIPT
                )
            )
    }

    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(MyArgumentsProvider::class)
    fun `should generate code (E2E)`(path: String, lang: GeneratorLanguage) { //TODO move to better place
        val module = module()

        val directory = codeGenerator.generateCode(module, lang)

        val directoryApi = DirectoryLogic()
        val exampleDirectory = directoryApi.readDirectory(Path(path))

        val compareResult = directoryApi.compare(directory, exampleDirectory)
        assertThat(compareResult.same)
            .withFailMessage(compareResult.differences.toString())
            .isTrue()
    }
}