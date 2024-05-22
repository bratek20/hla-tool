package pl.bratek20.hla.facade

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import pl.bratek20.architecture.context.someContextBuilder
import pl.bratek20.architecture.properties.impl.PropertiesModule
import pl.bratek20.architecture.properties.sources.inmemory.InMemoryPropertiesSource
import pl.bratek20.architecture.properties.sources.inmemory.InMemoryPropertiesSourceModule
import pl.bratek20.hla.directory.DirectoriesMock
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.context.DirectoriesMocks
import pl.bratek20.hla.directory.fixtures.assertDirectory
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.facade.api.*
import pl.bratek20.hla.facade.context.FacadeImpl
import pl.bratek20.hla.facade.fixtures.hlaProperties
import java.util.stream.Stream

class HlaFacadeTest {
    data class TestPaths(
        val exampleMainPath: String,
        val exampleTestFixturesPath: String,
        val expectedMainPathSuffix: String,
        val expectedTestFixturesPathSuffix: String
    )

    class MyArgumentsProvider : ArgumentsProvider {
        private fun kotlinTestPaths(packageName: String): TestPaths {
            return TestPaths(
                exampleMainPath = "../example/kotlin/src/main/kotlin/com/some/pkg/$packageName",
                exampleTestFixturesPath = "../example/kotlin/src/testFixtures/kotlin/com/some/pkg/$packageName",
                expectedMainPathSuffix = "/src/main/kotlin/com/some/pkg",
                expectedTestFixturesPathSuffix = "/src/testFixtures/kotlin/com/some/pkg"
            )
        }

        private fun typescriptTestPaths(moduleName: String): TestPaths {
            return TestPaths(
                exampleMainPath = "../example/typescript/main/$moduleName",
                exampleTestFixturesPath = "../example/typescript/test/$moduleName",
                expectedMainPathSuffix = "/main",
                expectedTestFixturesPathSuffix = "/test"
            )
        }

        private val KOTLIN_PROFILE = "kotlin"
        private val TYPE_SCRIPT_PROFILE = "typeScript"

        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    "OtherModule",
                    KOTLIN_PROFILE,
                    kotlinTestPaths("othermodule")
                ),
                Arguments.of(
                    "OtherModule",
                    TYPE_SCRIPT_PROFILE,
                    typescriptTestPaths("OtherModule")
                ),
                Arguments.of(
                    "SomeModule",
                    KOTLIN_PROFILE,
                    kotlinTestPaths("somemodule")
                ),
                Arguments.of(
                    "SomeModule",
                    TYPE_SCRIPT_PROFILE,
                    typescriptTestPaths("SomeModule")
                ),
                Arguments.of(
                    "TypesModule",
                    KOTLIN_PROFILE,
                    kotlinTestPaths("typesmodule")
                ),
                Arguments.of(
                    "TypesModule",
                    TYPE_SCRIPT_PROFILE,
                    typescriptTestPaths("TypesModule")
                ),
            )
        }
    }

    data class SetupResult(
        val directoriesMock: DirectoriesMock,
        val facade: HlaFacade
    )
    data class SetupArgs(
        var kotlinOnlyParts: List<String> = emptyList(),
    )
    private fun setup(init: (SetupArgs.() -> Unit) = {}): SetupResult {
        val args = SetupArgs().apply(init)
        val context = someContextBuilder()
            .withModules(
                DirectoriesMocks(),

                PropertiesModule(),
                InMemoryPropertiesSourceModule(),

                FacadeImpl(),
            )
            .build()

        val directoriesMock = context.get(DirectoriesMock::class.java)
        val propertiesSource = context.get(InMemoryPropertiesSource::class.java)

        val testProjectPath = "some/project/path"

        propertiesSource.set(
            PROPERTIES_KEY,
            hlaProperties {
                profiles = listOf(
                    {
                        name = "kotlin"
                        language = ModuleLanguage.KOTLIN
                        mainPath = "src/main/kotlin/com/some/pkg"
                        fixturesPath = "src/testFixtures/kotlin/com/some/pkg"
                        projectPath = testProjectPath
                        onlyParts = args.kotlinOnlyParts
                    },
                    {
                        name = "typeScript"
                        language = ModuleLanguage.TYPE_SCRIPT
                        mainPath = "main"
                        fixturesPath = "test"
                        projectPath = testProjectPath
                    },
                )
            }
        )

        val facade = context.get(HlaFacade::class.java)

        return SetupResult(directoriesMock, facade)
    }

    @ParameterizedTest(name = "{0} ({1})")
    @ArgumentsSource(MyArgumentsProvider::class)
    fun `should start module`(
        moduleName: String,
        profileName: String,
        paths: TestPaths
    ) {
        //given
        val (directoriesMock, facade) = setup()

        val hlaFolderPath = Path("../example/hla")

        //when
        facade.startModule(
            ModuleOperationArgs(
                moduleName = ModuleName(moduleName),
                profileName = ProfileName(profileName),
                hlaFolderPath = hlaFolderPath,
            )
        )

        //then
        directoriesMock.assertWriteCount(2)
        val mainDirectory = directoriesMock.assertWriteAndGetDirectory(
            1,
            "some/project/path" + paths.expectedMainPathSuffix
        )
        val testFixturesDirectory = directoriesMock.assertWriteAndGetDirectory(
            2,
            "some/project/path" + paths.expectedTestFixturesPathSuffix
        )

        assertWrittenDirectoryWithExample(mainDirectory, paths.exampleMainPath)
        assertWrittenDirectoryWithExample(testFixturesDirectory, paths.exampleTestFixturesPath)
    }

    private fun assertWrittenDirectoryWithExample(writtenDirectory: Directory, examplePath: String ) {
        val directories = DirectoriesLogic()
        val exampleDirectory = directories.readDirectory(Path(examplePath))

        val compareResult = directories.compare(writtenDirectory, exampleDirectory)
        val failMessage = "${compareResult.differences.size} differences found!\n" +
                compareResult.differences.joinToString("\n")

        assertThat(compareResult.same)
            .withFailMessage(failMessage)
            .isTrue()
    }

    @Test
    fun `should update module`() {
        //given
        val (directoriesMock, facade) = setup()

        val hlaFolderPath = Path("../example/hla")

        val args = ModuleOperationArgs(
            moduleName = ModuleName("SomeModule"),
            profileName = ProfileName("kotlin"),
            hlaFolderPath = hlaFolderPath,
        )
        facade.startModule(args)

        val expectedFilesToSkipUpdate = setOf(
            "api/CustomTypes",
            "api/CustomTypesMapper",
        )

        val expectedDirectoriesToSkipUpdate = setOf(
            "context",
            "impl"
        )

        //when
        facade.updateModule(args)

        //then
        directoriesMock.assertWriteCount(4)
        val mainDirectoryStart = directoriesMock.assertWriteAndGetDirectory(
            1,
            "some/project/path/src/main/kotlin/com/some/pkg"
        )
        val testFixturesDirectoryStart = directoriesMock.assertWriteAndGetDirectory(
            2,
            "some/project/path/src/testFixtures/kotlin/com/some/pkg"
        )

        val mainDirectoryUpdate = directoriesMock.assertWriteAndGetDirectory(
            3,
            "some/project/path/src/main/kotlin/com/some/pkg"
        )

        val testFixturesDirectoryUpdate = directoriesMock.assertWriteAndGetDirectory(
            4,
            "some/project/path/src/testFixtures/kotlin/com/some/pkg"
        )

        val mainCompareResult = DirectoriesLogic().compare(mainDirectoryStart, mainDirectoryUpdate)
        val testFixturesCompareResult = DirectoriesLogic().compare(testFixturesDirectoryStart, testFixturesDirectoryUpdate)

        val expectedMainDifference = expectedFilesToSkipUpdate.map {
                "File somemodule/$it.kt not found in second directory"
            } + expectedDirectoriesToSkipUpdate.map {
                "Directory somemodule/$it not found in second directory"
            }

        assertThat(mainCompareResult.differences).containsExactlyInAnyOrderElementsOf(
            expectedMainDifference
        )

        assertThat(testFixturesCompareResult.differences).isEmpty()
    }

    @Test
    fun `should start module using onlyParts`() {
        //given
        val (directoriesMock, facade) = setup {
            kotlinOnlyParts = listOf(
                "NamedTypes",
                "Properties",
                "Builders"
            )
        }

        val hlaFolderPath = Path("../example/hla")

        val args = ModuleOperationArgs(
            moduleName = ModuleName("SomeModule"),
            profileName = ProfileName("kotlin"),
            hlaFolderPath = hlaFolderPath,
        )

        //when
        facade.startModule(args)

        //then
        directoriesMock.assertWriteCount(2)
        val mainDirectory = directoriesMock.assertWriteAndGetDirectory(
            1,
            "some/project/path/src/main/kotlin/com/some/pkg"
        )
        val testFixturesDirectory = directoriesMock.assertWriteAndGetDirectory(
            2,
            "some/project/path/src/testFixtures/kotlin/com/some/pkg"
        )

        assertDirectory(mainDirectory) {
            hasDirectory = {
                name = "api"
                files = listOf(
                    {
                        name = "NamedTypes.kt"
                    },
                    {
                        name = "Properties.kt"
                    },
                )
            }
        }

        assertDirectory(testFixturesDirectory) {
            hasDirectory = {
                name = "fixtures"
                files = listOf {
                    name = "Builders.kt"
                }
            }
        }
    }
}