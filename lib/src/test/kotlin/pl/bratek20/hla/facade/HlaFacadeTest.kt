package pl.bratek20.hla.facade

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import pl.bratek20.architecture.context.stableContextBuilder
import pl.bratek20.hla.directory.DirectoriesMock
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.context.DirectoriesMocks
import pl.bratek20.hla.directory.fixtures.assertDirectory
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.facade.api.*
import pl.bratek20.hla.facade.context.FacadeImpl
import java.util.stream.Stream

class HlaFacadeTest {
    data class TestPaths(
        val exampleMainPath: String,
        val exampleTestFixturesPath: String,
        val expectedMainPath: String,
        val expectedFixturesPath: String
    )

    class MyArgumentsProvider : ArgumentsProvider {
        fun kotlinTestPaths(packageName: String): TestPaths {
            return TestPaths(
                exampleMainPath = "../example/kotlin/src/main/kotlin/com/some/pkg/$packageName",
                exampleTestFixturesPath = "../example/kotlin/src/testFixtures/kotlin/com/some/pkg/$packageName",
                expectedMainPath = "../kotlin/src/main/kotlin/com/some/pkg",
                expectedFixturesPath = "../kotlin/src/testFixtures/kotlin/com/some/pkg"
            )
        }

        fun typescriptTestPaths(moduleName: String): TestPaths {
            return TestPaths(
                exampleMainPath = "../example/typescript/main/$moduleName",
                exampleTestFixturesPath = "../example/typescript/test/$moduleName",
                expectedMainPath = "../typescript/main",
                expectedFixturesPath = "../typescript/test"
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

    private fun setup(): SetupResult {
        val context = stableContextBuilder()
            .withModules(
                DirectoriesMocks(),

                FacadeImpl(),
            )
            .build()

        val directoriesMock = context.get(DirectoriesMock::class.java)

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

        //when
        facade.startModule(
            ModuleOperationArgs(
                moduleName = ModuleName(moduleName),
                profileName = ProfileName(profileName),
                hlaFolderPath = hlaFolderPath(),
            )
        )

        //then
        directoriesMock.assertWriteCount(2)
        val mainDirectory = directoriesMock.assertWriteAndGetDirectory(
            1,
            paths.expectedMainPath
        )
        val testFixturesDirectory = directoriesMock.assertWriteAndGetDirectory(
            2,
            paths.expectedFixturesPath
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

        val args = ModuleOperationArgs(
            moduleName = ModuleName("SomeModule"),
            profileName = ProfileName("kotlin"),
            hlaFolderPath = hlaFolderPath(),
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
        val paths = MyArgumentsProvider().kotlinTestPaths("somemodule")
        directoriesMock.assertWriteCount(4)
        val mainDirectoryStart = directoriesMock.assertWriteAndGetDirectory(
            1,
            paths.expectedMainPath
        )
        val fixturesDirectoryStart = directoriesMock.assertWriteAndGetDirectory(
            2,
            paths.expectedFixturesPath
        )

        val mainDirectoryUpdate = directoriesMock.assertWriteAndGetDirectory(
            3,
            paths.expectedMainPath
        )

        val fixturesDirectoryUpdate = directoriesMock.assertWriteAndGetDirectory(
            4,
            paths.expectedFixturesPath
        )

        val mainCompareResult = DirectoriesLogic().compare(mainDirectoryStart, mainDirectoryUpdate)
        val testFixturesCompareResult = DirectoriesLogic().compare(fixturesDirectoryStart, fixturesDirectoryUpdate)

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
        val (directoriesMock, facade) = setup()

        val args = ModuleOperationArgs(
            moduleName = ModuleName("SomeModule"),
            profileName = ProfileName("kotlinOnlyParts"),
            hlaFolderPath = hlaFolderPath(),
        )

        //when
        facade.startModule(args)

        //then
        directoriesMock.assertWriteCount(2)
        val paths = MyArgumentsProvider().kotlinTestPaths("somemodule")
        val mainDirectory = directoriesMock.assertWriteAndGetDirectory(
            1,
            paths.expectedMainPath
        )
        val fixturesDirectory = directoriesMock.assertWriteAndGetDirectory(
            2,
            paths.expectedFixturesPath
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

        assertDirectory(fixturesDirectory) {
            hasDirectory = {
                name = "fixtures"
                files = listOf {
                    name = "Builders.kt"
                }
            }
        }
    }

    private fun hlaFolderPath(): Path {
        return Path("../example/hla")
    }

    @Test
    fun shouldUpdateAllModules() {
        //given
        val (directoriesMock, facade) = setup()

        val args = AllModulesOperationArgs(
            profileName = ProfileName("kotlin"),
            hlaFolderPath = hlaFolderPath(),
        )

        //when
        facade.updateAllModules(args)

        //then
        directoriesMock.assertWriteCount(6)
    }
}