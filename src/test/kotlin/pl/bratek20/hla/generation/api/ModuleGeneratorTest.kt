package pl.bratek20.hla.generation.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoryLogic
import pl.bratek20.hla.generation.impl.core.ModuleGeneratorImpl
import pl.bratek20.hla.model.TypeWrapper
import pl.bratek20.hla.model.hlaModule
import java.util.stream.Stream

class ModuleGeneratorTest {
    private val codeGenerator = ModuleGeneratorImpl()

    fun otherModule() = hlaModule {
        name = "OtherModule"
        simpleValueObjects = listOf {
            name = "OtherId"
            type = "string"
        }
        complexValueObjects = listOf {
            name = "OtherClass"
            fields = listOf(
                {
                    name = "id"
                    type = {
                        name = "OtherId"
                    }
                },
                {
                    name = "amount"
                    type = {
                        name = "int"
                    }
                }
            )
        }
    }

    fun someModule() = hlaModule {
        name = "SomeModule"
        simpleValueObjects = listOf {
            name = "SomeId"
            type = "string"
        }
        complexValueObjects = listOf(
            {
                name = "SomeClass"
                fields = listOf (
                    {
                        name = "id"
                        type = {
                            name = "SomeId"
                        }
                    },
                    {
                        name = "amount"
                        type = {
                            name = "int"
                        }
                    }
                )
            },
            {
                name = "SomeClass2"
                fields = listOf (
                    {
                        name = "id"
                        type = {
                            name = "SomeId"
                        }
                    },
                    {
                        name = "enabled"
                        type = {
                            name = "bool"
                        }
                    },
                    {
                        name = "names"
                        type = {
                            name = "string"
                            wrappers = listOf (
                                TypeWrapper.LIST
                            )
                        }
                    },
                    {
                        name = "ids"
                        type = {
                            name = "SomeId"
                            wrappers = listOf (
                                TypeWrapper.LIST
                            )
                        }
                    }
                )
            },
            {
                name = "SomeClass3"
                fields = listOf (
                    {
                        name = "class2Object"
                        type = {
                            name = "SomeClass2"
                        }
                    },
                    {
                        name = "class2List"
                        type = {
                            name = "SomeClass2"
                            wrappers = listOf (
                                TypeWrapper.LIST
                            )
                        }
                    }
                )
            },
            {
                name = "SomeClass4"
                fields = listOf (
                    {
                        name = "otherId"
                        type = {
                            name = "OtherId"
                        }
                    },
                    {
                        name = "otherClass"
                        type = {
                            name = "OtherClass"
                        }
                    },
                    {
                        name = "otherIdList"
                        type = {
                            name = "OtherId"
                            wrappers = listOf (
                                TypeWrapper.LIST
                            )
                        }
                    },
                    {
                        name = "otherClassList"
                        type = {
                            name = "OtherClass"
                            wrappers = listOf (
                                TypeWrapper.LIST
                            )
                        }
                    }
                )
            }
        )
        interfaces = listOf {
            name = "SomeInterface"
            methods = listOf (
                {
                    name = "someCommand"
                    returnType = null //TODO: make it work for null
                    args = listOf (
                        {
                            name = "id"
                            type = {
                                name = "SomeId"
                            }
                        },
                        {
                            name = "amount"
                            type = {
                                name = "int"
                            }
                        }
                    )
                },
                {
                    name = "someQuery"
                    returnType = {
                        name = "SomeClass"
                    }
                    args = listOf {
                        name = "id"
                        type = {
                            name = "SomeId"
                        }
                    }
                }
            )
        }
    }

    class MyArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> =
            Stream.of(
                Arguments.of(
                    "OtherModule",
                    "example/kotlin/src/main/java/pl/bratek20/othermodule",
                    ModuleLanguage.KOTLIN
                ),
                Arguments.of(
                    "OtherModule",
                    "example/typescript/OtherModule",
                    ModuleLanguage.TYPE_SCRIPT
                ),
                Arguments.of(
                    "SomeModule",
                    "example/kotlin/src/main/java/pl/bratek20/somemodule",
                    ModuleLanguage.KOTLIN
                ),
                Arguments.of(
                    "SomeModule",
                    "example/typescript/SomeModule",
                    ModuleLanguage.TYPE_SCRIPT
                ),
            )
    }

    @ParameterizedTest(name = "{0} ({2})")
    @ArgumentsSource(MyArgumentsProvider::class)
    fun `should generate module (E2E)`(moduleName: String, path: String, lang: ModuleLanguage) { //TODO move to better place
        val modules = listOf(
            someModule(),
            otherModule()
        )

        val directory = codeGenerator.generate(ModuleName(moduleName), lang, modules)

        val directoryApi = DirectoryLogic()
        val exampleDirectory = directoryApi.readDirectory(Path(path))

        val compareResult = directoryApi.compare(directory, exampleDirectory)
        val failMessage = "${compareResult.differences.size} differences found!\n" +
                compareResult.differences.joinToString("\n")

        assertThat(compareResult.same)
            .withFailMessage(failMessage)
            .isTrue()
    }
}