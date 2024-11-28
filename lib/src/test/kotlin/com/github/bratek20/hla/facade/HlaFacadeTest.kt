package com.github.bratek20.hla.facade

import com.github.bratek20.architecture.context.stableContextBuilder
import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.facade.context.FacadeImpl
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.hla.typesworld.fixtures.*
import com.github.bratek20.logs.LoggerMock
import com.github.bratek20.logs.LogsMocks
import com.github.bratek20.utils.directory.api.Directory
import com.github.bratek20.utils.directory.api.File
import com.github.bratek20.utils.directory.api.Path
import com.github.bratek20.utils.directory.context.DirectoriesMocks
import com.github.bratek20.utils.directory.fixtures.DirectoriesMock
import com.github.bratek20.utils.directory.fixtures.FilesMock
import com.github.bratek20.utils.directory.fixtures.assertDirectory
import com.github.bratek20.utils.directory.impl.DirectoriesLogic
import com.github.bratek20.utils.directory.impl.FilesLogic
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class HlaFacadeTest {


    data class TestPaths(
        val exampleMainPath: String,
        val exampleFixturesPath: String,
        val exampleTestsPath: String,
        val expectedMainPath: String,
        val expectedFixturesPath: String,
        val expectedTestsPath: String,
        val hlaFolderPath: String = "../example/hla"
    )

    class ShouldStartModuleArgsProvider : ArgumentsProvider {
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

        fun kotlin2TestPaths(packageName: String): TestPaths {
            return TestPaths(
                exampleMainPath = "../example/kotlin2/src/main/kotlin/com/other/pkg/$packageName",
                exampleFixturesPath = "../example/kotlin2/src/testFixtures/kotlin/com/other/pkg/$packageName",
                exampleTestsPath = "../example/kotlin2/src/test/kotlin/com/other/pkg/$packageName",
                expectedMainPath = "../example/hla2/../kotlin2/src/main/kotlin/com/other/pkg",
                expectedFixturesPath = "../example/hla2/../kotlin2/src/testFixtures/kotlin/com/other/pkg",
                expectedTestsPath = "../example/hla2/../kotlin2/src/test/kotlin/com/other/pkg",
                hlaFolderPath = "../example/hla2"
            )
        }

        private val KOTLIN_PROFILE = "kotlin"
        private val KOTLIN_2_PROFILE = "kotlin2"

        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    "OtherModule",
                    KOTLIN_PROFILE,
                    kotlinTestPaths("othermodule")
                ),
                Arguments.of(
                    "SomeModule",
                    KOTLIN_PROFILE,
                    kotlinTestPaths("somemodule")
                ),
                Arguments.of(
                    "TypesModule",
                    KOTLIN_PROFILE,
                    kotlinTestPaths("typesmodule")
                ),
                Arguments.of(
                    "SimpleModule",
                    KOTLIN_PROFILE,
                    kotlinTestPaths("simplemodule")
                ),
                Arguments.of(
                    "ImportingModule",
                    KOTLIN_2_PROFILE,
                    kotlin2TestPaths("importingmodule")
                ),
            )
        }
    }

    class ShouldStartTypeScriptModuleArgsProvider: ArgumentsProvider {
        fun typescriptTestPaths(moduleName: String): TestPaths {
            return TestPaths(
                exampleMainPath = "../example/typescript/main/$moduleName",
                exampleFixturesPath = "../example/typescript/Tests/$moduleName",
                exampleTestsPath = "../example/typescript/Tests/$moduleName",
                expectedMainPath = "../example/hla/../typescript/main",
                expectedFixturesPath = "../example/hla/../typescript/Tests",
                expectedTestsPath = "../example/hla/../typescript/Tests",
            )
        }

        fun typescript2TestPaths(moduleName: String): TestPaths {
            return TestPaths(
                exampleMainPath = "../example/typescript2/main/$moduleName",
                exampleFixturesPath = "../example/typescript2/Tests/$moduleName",
                exampleTestsPath = "../example/typescript2/Tests/$moduleName",
                expectedMainPath = "../example/hla2/../typescript2/main",
                expectedFixturesPath = "../example/hla2/../typescript2/Tests",
                expectedTestsPath = "../example/hla2/../typescript2/Tests",
                hlaFolderPath = "../example/hla2"
            )
        }

        private val TYPE_SCRIPT_PROFILE = "typeScript"
        private val TYPE_SCRIPT_2_PROFILE = "typeScript2"

        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    "OtherModule",
                    TYPE_SCRIPT_PROFILE,
                    typescriptTestPaths("OtherModule")
                ),
                Arguments.of(
                    "SomeModule",
                    TYPE_SCRIPT_PROFILE,
                    typescriptTestPaths("SomeModule")
                ),
                Arguments.of(
                    "TypesModule",
                    TYPE_SCRIPT_PROFILE,
                    typescriptTestPaths("TypesModule")
                ),
                Arguments.of(
                    "SimpleModule",
                    TYPE_SCRIPT_PROFILE,
                    typescriptTestPaths("SimpleModule")
                ),
                Arguments.of(
                    "ImportingModule",
                    TYPE_SCRIPT_2_PROFILE,
                    typescript2TestPaths("ImportingModule")
                ),
            )
        }
    }

    //TODO-REF merge with ShouldStartModuleArgsProvider when cSharp will be fully integrated
    class ShouldStartCSharpModuleArgsProvider : ArgumentsProvider {
        fun cSharpTestPaths(moduleName: String): TestPaths {
            return TestPaths(
                exampleMainPath = "../example/c-sharp/$moduleName",
                exampleFixturesPath = "../example/c-sharp/Tests/Fixtures/$moduleName",
                exampleTestsPath = "../example/c-sharp/Tests/Test/$moduleName",
                expectedMainPath = "../example/hla/../c-sharp",
                expectedFixturesPath = "../example/hla/../c-sharp/Tests/Fixtures",
                expectedTestsPath = "../example/hla/../c-sharp/Tests/Test",
            )
        }

        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    "OtherModule",
                    C_SHARP_PROFILE,
                    cSharpTestPaths("OtherModule")
                ),
                Arguments.of(
                    "SomeModule",
                    C_SHARP_PROFILE,
                    cSharpTestPaths("SomeModule")
                ),
            )
        }

        companion object {
            val C_SHARP_PROFILE = "cSharp"
        }
    }

    data class SetupResult(
        val directoriesMock: DirectoriesMock,
        val facade: HlaFacade,
        val filesMock: FilesMock,
        val loggerMock: LoggerMock,
        val typesWorldApi: TypesWorldApi
    )

    private fun setup(): SetupResult {
        val context = stableContextBuilder()
            .withModules(
                DirectoriesMocks(),

                LogsMocks(),

                FacadeImpl(),
            )
            .build()

        val directoriesMock = context.get(DirectoriesMock::class.java)
        val filesMock = context.get(FilesMock::class.java)

        val loggerMock = context.get(LoggerMock::class.java)

        val facade = context.get(HlaFacade::class.java)

        return SetupResult(
            directoriesMock,
            facade,
            filesMock,
            loggerMock,
            typesWorldApi = context.get(TypesWorldApi::class.java)
        )
    }

    @ParameterizedTest(name = "{0} ({1})")
    @ArgumentsSource(ShouldStartModuleArgsProvider::class)
    fun `should start module`(
        moduleName: String,
        profileName: String,
        paths: TestPaths
    ) {
        //given
        val (directoriesMock, facade) = setup()

        //when
        facade.startModule(
            ModuleOperationArgs.create(
                moduleName = ModuleName(moduleName),
                profileName = ProfileName(profileName),
                hlaFolderPath = Path(paths.hlaFolderPath),
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

    @ParameterizedTest(name = "{0} ({1})")
    @ArgumentsSource(ShouldStartTypeScriptModuleArgsProvider::class)
    fun `should start TypeScript module`(
        moduleName: String,
        profileName: String,
        paths: TestPaths
    ) {
        //given
        val (directoriesMock, facade) = setup()

        //when
        facade.startModule(
            ModuleOperationArgs.create(
                moduleName = ModuleName(moduleName),
                profileName = ProfileName(profileName),
                hlaFolderPath = Path(paths.hlaFolderPath),
            )
        )

        //then
        directoriesMock.assertWriteCount(2)
        val mainDirectory = directoriesMock.assertWriteAndGetDirectory(
            1,
            paths.expectedMainPath
        )
        val testsDirectory = directoriesMock.assertWriteAndGetDirectory(
            2,
            paths.expectedTestsPath
        )

        assertWrittenDirectoryWithExample(mainDirectory, paths.exampleMainPath)
        assertWrittenDirectoryWithExample(testsDirectory, paths.exampleTestsPath)
    }

    @ParameterizedTest(name = "{0} ({1})")
    @ArgumentsSource(ShouldStartCSharpModuleArgsProvider::class)
    fun `should start C# module`(
        moduleName: String,
        profileName: String,
        paths: TestPaths
    ) {
        //given
        val (directoriesMock, facade) = setup()

        //when
        facade.startModule(
            ModuleOperationArgs.create(
                moduleName = ModuleName(moduleName),
                profileName = ProfileName(profileName),
                hlaFolderPath = Path(paths.hlaFolderPath),
            )
        )

        //then
        directoriesMock.assertWriteCount(1)
        val mainDirectory = directoriesMock.assertWriteAndGetDirectory(
            1,
            paths.expectedMainPath
        )
//        val fixturesDirectory = directoriesMock.assertWriteAndGetDirectory(
//            2,
//            paths.expectedFixturesPath
//        )
//        val testsDirectory = directoriesMock.assertWriteAndGetDirectory(
//            3,
//            paths.expectedTestsPath
//        )

        assertWrittenDirectoryWithExample(mainDirectory, paths.exampleMainPath)
        //assertWrittenDirectoryWithExample(fixturesDirectory, paths.exampleFixturesPath)
//        assertWrittenDirectoryWithExample(testsDirectory, paths.exampleTestsPath)
    }

    @Test
    fun `should populate types world`() {
        //given
        val moduleName: String = "OtherModule"
        val profileName: String = ShouldStartCSharpModuleArgsProvider.C_SHARP_PROFILE
        val paths: TestPaths = ShouldStartCSharpModuleArgsProvider().cSharpTestPaths("OtherModule")
        val sr = setup()

        //when
        sr.facade.startModule(
            ModuleOperationArgs.create(
                moduleName = ModuleName(moduleName),
                profileName = ProfileName(profileName),
                hlaFolderPath = Path(paths.hlaFolderPath),
            )
        )

        //then
        val assertHasTypeOfName = { typeName: String ->
            assertThatCode {
                sr.typesWorldApi.getTypeByName(WorldTypeName(typeName))
            }
            .withFailMessage("Type with name $typeName not found")
            .doesNotThrowAnyException()
        }
        val assertHasType = { typeName: String, typePath: String ->
            assertThat(sr.typesWorldApi.hasType(worldType {
                name = typeName
                path = typePath
            }))
            .withFailMessage("Type $typeName not found")
            .isTrue()
        }

        val assertHasNotType = { typeName: String, typePath: String ->
            assertThat(sr.typesWorldApi.hasType(worldType {
                name = typeName
                path = typePath
            }))
            .withFailMessage("Type $typeName found")
            .isFalse()
        }

        val assertHasClassType = { typeName: String, typePath: String, expectedClass: ExpectedWorldClassType.() -> Unit ->
            assertHasType(typeName, typePath)
            sr.typesWorldApi.getClassType(WorldType(typeName, typePath)).let {
                assertWorldClassType(it, expectedClass)
            }
        }

        val assertHasConcreteParametrizedClass = { typeName: String, typePath: String, expectedClass: ExpectedWorldConcreteParametrizedClass.() -> Unit ->
            assertHasType(typeName, typePath)
            sr.typesWorldApi.getConcreteParametrizedClass(WorldType(typeName, typePath)).let {
                assertWorldConcreteParametrizedClass(it, expectedClass)
            }
        }

        //special types
        assertHasType("int", "Language/Types/Api/Primitives")
        assertHasType("string", "Language/Types/Api/Primitives")

        assertHasType("List<int>", "Language/Types/Api/Primitives")
        assertHasType("Optional<int>", "Language/Types/Api/Primitives")

        //api types
        assertHasClassType("OtherClass", "OtherModule/Api/ValueObjects") {
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

        assertHasClassType("ClassWithEnumList", "SomeModule/Api/ValueObjects") {
            fields = listOf {
                name = "enumList"
                type = {
                    name = "List<SomeEnum2>"
                }
            }
        }

        //b20 view model types
        assertHasType("EmptyModel", "B20/Frontend/UiElements/Api/ValueObjects")

        //TODO-FIX it should not pass
        assertHasType("Label", "OtherModule/ViewModel/GeneratedElements")
        //assertHasNotType("Label", "OtherModule/ViewModel/GeneratedElements")

        //modules view model types
        assertHasConcreteParametrizedClass("UiElement<OtherClass>", "OtherModule/ViewModel/GeneratedElements") {
            typeArguments = listOf {
                name = "OtherClass"
            }
        }

        assertHasClassType("OtherClassVm", "OtherModule/ViewModel/GeneratedElements") {
            extends = {
                name = "UiElement<OtherClass>"
            }
            fields = listOf(
                {
                    name = "id"
                    type = {
                        name = "Label"
                        //path = "B20/Frontend/UiElements" //TODO-FIX
                    }
                },
                {
                    name = "amount"
                    type = {
                        name = "Label"
                    }
                }
            )
        }

        assertHasClassType("ClassWithEnumListVm", "SomeModule/ViewModel/GeneratedElements") {
            fields = listOf {
                name = "enumList"
                type = {
                    name = "SomeEnum2SwitchGroup"
                }
            }
        }
        assertHasType("SomeEnum2SwitchGroup", "SomeModule/ViewModel/GeneratedElements")
        assertHasType("SomeEnum2Switch", "SomeModule/ViewModel/GeneratedElements")
        assertHasClassType("OtherClassVmGroup", "OtherModule/ViewModel/GeneratedElements") {

        }
//        assertHasClassType("SomeEnum2SwitchGroup", "SomeModule/ViewModel/GeneratedElements") {
//            extends = {
//                name = "UiElementGroup<SomeEnum2Switch,SomeEnum>"
//            }
//        }

        //view types
        assertHasType("OtherClassView", "OtherModule/View/ElementsView")
    }

    @Test
    fun `should update Prefabs submodule`() {
        //given
        val (directoriesMock, facade) = setup()

        val args = ModuleOperationArgs.create(
            moduleName = ModuleName("SomeModule"),
            profileName = ProfileName(ShouldStartCSharpModuleArgsProvider.C_SHARP_PROFILE),
            hlaFolderPath = hlaFolderPath(),
        )

        //when
        facade.updateModule(args)

        //then
        val dir = directoriesMock.assertWriteAndGetDirectory(
            1,
            "../example/hla/../c-sharp"
        )

        assertThat(dir.getDirectories().map { it.getName().value })
            .contains("Prefabs")
    }

    private fun assertWrittenDirectoryWithExample(writtenDirectory: Directory, examplePath: String ) {
        val directories = DirectoriesLogic()
        val exampleDirectory = directories.read(Path(examplePath))

        val compareResult = directories.compare(writtenDirectory, exampleDirectory)
        val failMessage = "${compareResult.getDifferences().size} differences found!\n" +
                compareResult.getDifferences().joinToString("\n")

        assertThat(compareResult.getSame())
            .withFailMessage(failMessage)
            .isTrue()
    }

    private fun assertWrittenFileWithExample(writtenFile: File, examplePath: String ) {
        val files = FilesLogic()
        val exampleFile = files.read(Path(examplePath))

        val compareResult = files.compare(writtenFile, exampleFile)
        val failMessage = "${compareResult.getDifferences().size} differences found!\n" +
                compareResult.getDifferences().joinToString("\n")

        assertThat(compareResult.getSame())
            .withFailMessage(failMessage)
            .isTrue()
    }

    @Test
    fun `should update module`() {
        //given
        val (directoriesMock, facade) = setup()

        val args = ModuleOperationArgs.create(
            moduleName = ModuleName("SomeModule"),
            profileName = ProfileName("kotlin"),
            hlaFolderPath = hlaFolderPath(),
        )
        facade.startModule(args)

        val expectedMainFilesToSkipUpdate = setOf(
            "api/CustomTypes",
            "api/CustomTypesMapper",
            "impl/Logic",
            "context/Impl",
        )

        val expectedMainDirectoriesToSkipUpdate = setOf<String>(
        )

        val expectedFixturesFilesToSkipUpdate = setOf<String>(
            //"fixtures/Mocks",
        )

        //tests directory is not updated

        //when
        facade.updateModule(args)

        //then
        val paths = ShouldStartModuleArgsProvider().kotlinTestPaths("somemodule")
        directoriesMock.assertWriteCount(5)

        val mainDirectoryStart = directoriesMock.assertWriteAndGetDirectory(
            1,
            paths.expectedMainPath
        )
        val fixturesDirectoryStart = directoriesMock.assertWriteAndGetDirectory(
            2,
            paths.expectedFixturesPath
        )
//        val testsDirectoryStart = directoriesMock.assertWriteAndGetDirectory(
//            3,
//            paths.expectedTestsPath
//        )

        val mainDirectoryUpdate = directoriesMock.assertWriteAndGetDirectory(
            4,
            paths.expectedMainPath
        )
        val fixturesDirectoryUpdate = directoriesMock.assertWriteAndGetDirectory(
            5,
            paths.expectedFixturesPath
        )

        val mainCompareResult = DirectoriesLogic().compare(mainDirectoryStart, mainDirectoryUpdate)
        val fixturesCompareResult = DirectoriesLogic().compare(fixturesDirectoryStart, fixturesDirectoryUpdate)

        val expectedMainDifference = expectedMainFilesToSkipUpdate.map {
                "File somemodule/$it.kt not found in second directory"
            } + expectedMainDirectoriesToSkipUpdate.map {
                "Directory somemodule/$it not found in second directory"
            }

        val expectedFixturesDifference = expectedFixturesFilesToSkipUpdate.map {
            "File somemodule/$it.kt not found in second directory"
        }

        assertThat(mainCompareResult.getDifferences()).containsExactlyInAnyOrderElementsOf(
            expectedMainDifference
        )

        assertThat(fixturesCompareResult.getDifferences()).containsExactlyInAnyOrderElementsOf(
            expectedFixturesDifference
        )
    }

    @Nested
    inner class LogsScope {
        private lateinit var facade: HlaFacade
        private lateinit var loggerMock: LoggerMock

        private val args = ModuleOperationArgs.create(
            moduleName = ModuleName("SomeModule"),
            profileName = ProfileName("kotlin"),
            hlaFolderPath = hlaFolderPath(),
        )

        @BeforeEach
        fun beforeEach() {
            val result = setup()
            facade = result.facade
            loggerMock = result.loggerMock
        }

        @Test
        fun `should log what is happening on start`() {
            //when
            facade.startModule(args)

            //then
            loggerMock.assertInfos(
                "Starting module SomeModule with profile kotlin",
                "Parsing module OtherModule",
                "Parsing module SimpleModule",
                "Parsing module SomeModule",
                "Parsing module TypesModule",
                "SomeModule/Api/Enums.kt generated",
                "SomeModule/Api/CustomTypes.kt generated",
                "SomeModule/Api/CustomTypesMapper.kt generated",
                "SomeModule/Api/SerializedCustomTypes.kt generated",
                "SomeModule/Api/ValueObjects.kt generated",
                "SomeModule/Api/DataClasses.kt generated",
                "SomeModule/Api/PropertyKeys.kt generated",
                "SomeModule/Api/DataKeys.kt generated",
                "SomeModule/Api/Exceptions.kt generated",
                "SomeModule/Api/Events.kt generated",
                "SomeModule/Api/Interfaces.kt generated",
                "SomeModule/Impl/DataClasses.kt generated",
                "SomeModule/Impl/DataKeys.kt generated",
                "SomeModule/Impl/Logic.kt generated",
                "SomeModule/Web/WebCommon.kt generated",
                "SomeModule/Web/WebClient.kt generated",
                "SomeModule/Web/WebServer.kt generated",
                "SomeModule/Context/Impl.kt generated",
                "SomeModule/Context/Web.kt generated",
                "SomeModule/Fixtures/Builders.kt generated",
                "SomeModule/Fixtures/Diffs.kt generated",
                "SomeModule/Fixtures/Asserts.kt generated",
                "SomeModule/Tests/ImplTest.kt generated",
            )
        }

        @Test
        fun `should log what is happening on update`() {
            //when
            facade.updateModule(args)

            //then
            loggerMock.assertInfos(
                "Updating module SomeModule with profile kotlin",
                "Parsing module OtherModule",
                "Parsing module SimpleModule",
                "Parsing module SomeModule",
                "Parsing module TypesModule",
                "SomeModule/Api/Enums.kt updated",
                "SomeModule/Api/SerializedCustomTypes.kt updated",
                "SomeModule/Api/ValueObjects.kt updated",
                "SomeModule/Api/DataClasses.kt updated",
                "SomeModule/Api/PropertyKeys.kt updated",
                "SomeModule/Api/DataKeys.kt updated",
                "SomeModule/Api/Exceptions.kt updated",
                "SomeModule/Api/Events.kt updated",
                "SomeModule/Api/Interfaces.kt updated",
                "SomeModule/Impl/DataClasses.kt updated",
                "SomeModule/Impl/DataKeys.kt updated",
                "SomeModule/Web/WebCommon.kt updated",
                "SomeModule/Web/WebClient.kt updated",
                "SomeModule/Web/WebServer.kt updated",
                "SomeModule/Context/Web.kt updated",
                "SomeModule/Fixtures/Builders.kt updated",
                "SomeModule/Fixtures/Diffs.kt updated",
                "SomeModule/Fixtures/Asserts.kt updated",
            )
        }
    }


    @Test
    fun `should start module using onlyPatterns`() {
        //given
        val (directoriesMock, facade) = setup()

        val args = ModuleOperationArgs.create(
            moduleName = ModuleName("SomeModule"),
            profileName = ProfileName("kotlinOnlyPatterns"),
            hlaFolderPath = hlaFolderPath(),
        )

        //when
        facade.startModule(args)

        //then
        directoriesMock.assertWriteCount(2)
        val paths = ShouldStartModuleArgsProvider().kotlinTestPaths("somemodule")
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
        val valueObjectsFile  = mainDirectory.getDirectories().find { it.getName().value == "api" }!!.getFiles().find { file -> file.getName().value == "ValueObjects.kt" }
        val buildersFile  = fixturesDirectory.getDirectories().find { it.getName().value == "fixtures" }!!.getFiles().find { file -> file.getName().value == "Builders.kt" }

        assertWrittenFileWithExample(valueObjectsFile!!, paths.exampleMainPath + "/api/ValueObjects.kt")
        assertWrittenFileWithExample(buildersFile!!, paths.exampleFixturesPath + "/fixtures/Builders.kt")
    }

    private fun hlaFolderPath(): Path {
        return Path("../example/hla")
    }

    @Test
    fun shouldUpdateAllModules() {
        //given
        val (directoriesMock, facade) = setup()

        val args = AllModulesOperationArgs.create(
            profileName = ProfileName("kotlin"),
            hlaFolderPath = hlaFolderPath(),
        )

        //when
        facade.updateAllModules(args)

        //then
        directoriesMock.assertWriteCount(8)
    }

    @Test
    fun shouldModifyTypeScriptFilesOnStart() {
        //given
        val (_, facade, filesMock) = setup()

        val args = ModuleOperationArgs.create(
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

        val args = ModuleOperationArgs.create(
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

        val args = ModuleOperationArgs.create(
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