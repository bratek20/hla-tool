package pl.bratek20.hla.facade

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import pl.bratek20.architecture.context.someContextBuilder
import pl.bratek20.architecture.properties.impl.PropertiesModule
import pl.bratek20.architecture.properties.sources.inmemory.InMemoryPropertiesSource
import pl.bratek20.architecture.properties.sources.inmemory.InMemoryPropertiesSourceModule
import pl.bratek20.hla.facade.api.ModuleName
import pl.bratek20.hla.directory.DirectoriesMock
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.context.DirectoriesMocks
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

        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            return Stream.of(
                Arguments.of(
                    "OtherModule",
                    ModuleLanguage.KOTLIN,
                    kotlinTestPaths("othermodule")
                ),
                Arguments.of(
                    "OtherModule",
                    ModuleLanguage.TYPE_SCRIPT,
                    typescriptTestPaths("OtherModule")
                ),
                Arguments.of(
                    "SomeModule",
                    ModuleLanguage.KOTLIN,
                    kotlinTestPaths("somemodule")
                ),
                Arguments.of(
                    "SomeModule",
                    ModuleLanguage.TYPE_SCRIPT,
                    typescriptTestPaths("SomeModule")
                ),
                Arguments.of(
                    "TypesModule",
                    ModuleLanguage.KOTLIN,
                    kotlinTestPaths("typesmodule")
                ),
                Arguments.of(
                    "TypesModule",
                    ModuleLanguage.TYPE_SCRIPT,
                    typescriptTestPaths("TypesModule")
                ),
            )
        }
    }

    @ParameterizedTest(name = "{0} ({1})")
    @ArgumentsSource(MyArgumentsProvider::class)
    fun `should generate module`(
        moduleName: String,
        lang: ModuleLanguage,
        paths: TestPaths
    ) {
        //given
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

        propertiesSource.set(
            PROPERTIES_KEY,
            hlaProperties {
                generateWeb = true
                kotlin = {
                    rootPackage = "com.some.pkg"
                }
                typeScript = {
                    srcPath = "main"
                    testPath = "test"
                }
            }
        )

        val facade = context.get(HlaFacade::class.java)

        val hlaFolderPath = Path("../example/hla")
        val projectPath = Path("some/project/path")

        //when
        facade.generateModule(
            GenerateModuleArgs(
                moduleName = ModuleName(moduleName),
                language = lang,
                hlaFolderPath = hlaFolderPath,
                projectPath = projectPath
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

        Assertions.assertThat(compareResult.same)
            .withFailMessage(failMessage)
            .isTrue()
    }
}