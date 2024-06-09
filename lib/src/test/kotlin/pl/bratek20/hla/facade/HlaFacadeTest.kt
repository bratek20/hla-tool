package pl.bratek20.hla.facade

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import pl.bratek20.architecture.context.stableContextBuilder
import pl.bratek20.hla.directory.fixtures.DirectoriesMock
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.context.DirectoriesMocks
import pl.bratek20.hla.directory.fixtures.FilesMock
import pl.bratek20.hla.directory.fixtures.assertDirectory
import pl.bratek20.hla.directory.fixtures.assertDirectoryExt
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.directory.impl.FilesLogic
import pl.bratek20.hla.facade.api.*
import pl.bratek20.hla.facade.context.FacadeImpl
import java.util.stream.Stream

class HlaFacadeTest {
    data class TestPaths(
        val exampleMainPath: String,
        val exampleFixturesPath: String,
        val exampleTestsPath: String,
        val expectedMainPath: String,
        val expectedFixturesPath: String,
        val expectedTestsPath: String
    )

    class MyArgumentsProvider : ArgumentsProvider {
        fun kotlinTestPaths(packageName: String): TestPaths {
            return TestPaths(
                exampleMainPath = "../example/kotlin/src/main/kotlin/com/some/pkg/$packageName",
                exampleFixturesPath = "../example/kotlin/src/testFixtures/kotlin/com/some/pkg/$packageName",
                exampleTestsPath = "../example/kotlin/src/test/kotlin/com/some/pkg/$packageName",
                expectedMainPath = "../example/hla/../kotlin/src/main/kotlin/com/some/pkg",
                expectedFixturesPath = "../example/hla/../kotlin/src/testFixtures/kotlin/com/some/pkg",
                expectedTestsPath = "../example/hla/../kotlin/src/test/kotlin/com/some/pkg",
            )
        }

        fun typescriptTestPaths(moduleName: String): TestPaths {
            return TestPaths(
                exampleMainPath = "../example/typescript/main/$moduleName",
                exampleFixturesPath = "../example/typescript/fixtures/$moduleName",
                exampleTestsPath = "../example/typescript/test/$moduleName",
                expectedMainPath = "../example/hla/../typescript/main",
                expectedFixturesPath = "../example/hla/../typescript/fixtures",
                expectedTestsPath = "../example/hla/../typescript/test",
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
        val facade: HlaFacade,
        val filesMock: FilesMock,
    )

    private fun setup(): SetupResult {
        val context = stableContextBuilder()
            .withModules(
                DirectoriesMocks(),

                FacadeImpl(),
            )
            .build()

        val directoriesMock = context.get(DirectoriesMock::class.java)
        val filesMock = context.get(FilesMock::class.java)

        val facade = context.get(HlaFacade::class.java)

        return SetupResult(directoriesMock, facade, filesMock)
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
        directoriesMock.assertWriteCount(3)
        val mainDirectory = directoriesMock.assertWriteAndGetDirectory(
            1,
            paths.expectedMainPath
        )
        val fixturesDirectory = directoriesMock.assertWriteAndGetDirectory(
            2,
            paths.expectedFixturesPath
        )
        val testsDirectory = directoriesMock.assertWriteAndGetDirectory(
            3,
            paths.expectedTestsPath
        )

        assertWrittenDirectoryWithExample(mainDirectory, paths.exampleMainPath)
        assertWrittenDirectoryWithExample(fixturesDirectory, paths.exampleFixturesPath)
        assertWrittenDirectoryWithExample(testsDirectory, paths.exampleTestsPath)
    }

    private fun assertWrittenDirectoryWithExample(writtenDirectory: Directory, examplePath: String ) {
        val directories = DirectoriesLogic()
        val exampleDirectory = directories.read(Path(examplePath))

        val compareResult = directories.compare(writtenDirectory, exampleDirectory)
        val failMessage = "${compareResult.differences.size} differences found!\n" +
                compareResult.differences.joinToString("\n")

        assertThat(compareResult.same)
            .withFailMessage(failMessage)
            .isTrue()
    }

    private fun assertWrittenFileWithExample(writtenFile: File, examplePath: String ) {
        val files = FilesLogic()
        val exampleFile = files.read(Path(examplePath))

        val compareResult = files.compare(writtenFile, exampleFile)
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

        val expectedMainFilesToSkipUpdate = setOf(
            "api/CustomTypes",
            "api/CustomTypesMapper",
            "impl/Logic",
        )

        val expectedMainDirectoriesToSkipUpdate = setOf(
            "context",
        )

        val expectedTestDirectoriesToSkipUpdate = setOf(
            "tests"
        )

        //when
        facade.updateModule(args)

        //then
        val paths = MyArgumentsProvider().kotlinTestPaths("somemodule")
        directoriesMock.assertWriteCount(6)

        val mainDirectoryStart = directoriesMock.assertWriteAndGetDirectory(
            1,
            paths.expectedMainPath
        )
        val fixturesDirectoryStart = directoriesMock.assertWriteAndGetDirectory(
            2,
            paths.expectedFixturesPath
        )
        val testsDirectoryStart = directoriesMock.assertWriteAndGetDirectory(
            3,
            paths.expectedTestsPath
        )

        val mainDirectoryUpdate = directoriesMock.assertWriteAndGetDirectory(
            4,
            paths.expectedMainPath
        )
        val fixturesDirectoryUpdate = directoriesMock.assertWriteAndGetDirectory(
            5,
            paths.expectedFixturesPath
        )
        val testsDirectoryUpdate = directoriesMock.assertWriteAndGetDirectory(
            6,
            paths.expectedTestsPath
        )

        val mainCompareResult = DirectoriesLogic().compare(mainDirectoryStart, mainDirectoryUpdate)
        val fixturesCompareResult = DirectoriesLogic().compare(fixturesDirectoryStart, fixturesDirectoryUpdate)
        val testsCompareResult = DirectoriesLogic().compare(testsDirectoryStart, testsDirectoryUpdate)

        val expectedMainDifference = expectedMainFilesToSkipUpdate.map {
                "File somemodule/$it.kt not found in second directory"
            } + expectedMainDirectoriesToSkipUpdate.map {
                "Directory somemodule/$it not found in second directory"
            }

        assertThat(mainCompareResult.differences).containsExactlyInAnyOrderElementsOf(
            expectedMainDifference
        )

        assertThat(fixturesCompareResult.differences).isEmpty()

        val expectedTestsDifference = expectedTestDirectoriesToSkipUpdate.map {
            "Directory somemodule/$it not found in second directory"
        }
        assertThat(testsCompareResult.differences).containsExactlyInAnyOrderElementsOf(
            expectedTestsDifference
        )
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
            directories = listOf {
                name = "api"
                files = listOf {
                    name = "ValueObjects.kt"
                }
            }
        }

        assertDirectory(fixturesDirectory) {
            directories = listOf {
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
        directoriesMock.assertWriteCount(9)
    }

    @Test
    fun shouldUpdateRespectingGenerateWebProperty() {
        //given
        val (directoriesMock, facade) = setup()

        val args = ModuleOperationArgs(
            moduleName = ModuleName("SomeModule"),
            profileName = ProfileName("kotlinNoWeb"),
            hlaFolderPath = hlaFolderPath(),
        )

        //when
        facade.updateModule(args)

        //then
        directoriesMock.assertWriteCount(3)

        val paths = MyArgumentsProvider().kotlinTestPaths("somemodule")
        val mainDirectory = directoriesMock.assertWriteAndGetDirectory(1, paths.expectedMainPath)
        assertDirectoryExt(mainDirectory) {
            hasNoDirectories = "web"
        }
    }

    @Test
    fun shouldModifyTypeScriptFilesOnStart() {
        //given
        val (_, facade, filesMock) = setup()

        val args = ModuleOperationArgs(
            moduleName = ModuleName("OtherModule"),
            profileName = ProfileName("typeScriptFileModifiers"),
            hlaFolderPath = hlaFolderPath(),
        )

        //when
        facade.startModule(args)

        //then
        filesMock.assertWriteCount(4)

        assertFileModification(
            filesMock,
            1,
            "../example/typescriptFileModifiers/afterStart/tsconfig.json",
            "../example/hla/../typescriptFileModifiers/beforeStart",
            "tsconfig.json"
        )

        assertFileModification(
            filesMock,
            2,
            "../example/typescriptFileModifiers/afterStart/Tests/tsconfig.json",
            "../example/hla/../typescriptFileModifiers/beforeStart/Tests",
            "tsconfig.json"
        )

        assertFileModification(
            filesMock,
            3,
            "../example/typescriptFileModifiers/afterStart/package.json",
            "../example/hla/../typescriptFileModifiers/beforeStart",
            "package.json"
        )

        assertFileModification(
            filesMock,
            4,
            "../example/typescriptFileModifiers/afterStart/launch.json",
            "../example/hla/../typescriptFileModifiers/beforeStart",
            "launch.json"
        )
    }

    private fun assertFileModification(
        filesMock: FilesMock,
        writeNumber: Int,
        examplePath: String,
        expectedPath: String,
        fileName: String
    ) {
        val file = filesMock.assertWriteAndGetFile(
            writeNumber,
            expectedPath,
            fileName
        )
        assertWrittenFileWithExample(file, examplePath)
    }

    @Test
    fun shouldNotModifyTypeScriptFilesOnUpdate() {
        //given
        val (_, facade, filesMock) = setup()

        val args = ModuleOperationArgs(
            moduleName = ModuleName("OtherModule"),
            profileName = ProfileName("typeScriptFileModifiers"),
            hlaFolderPath = hlaFolderPath(),
        )

        //when
        facade.updateModule(args)

        //then
        filesMock.assertWriteCount(0)
    }

    @Test
    fun shouldNotModifyTypeScriptFilesOnStartIfAlreadyChangesThere() {
        //given
        val (_, facade, filesMock) = setup()

        val args = ModuleOperationArgs(
            moduleName = ModuleName("OtherModule"),
            profileName = ProfileName("typeScriptFileModifiersAfterStart"),
            hlaFolderPath = hlaFolderPath(),
        )

        //when
        facade.startModule(args)

        //then
        filesMock.assertWriteCount(0)
    }
}