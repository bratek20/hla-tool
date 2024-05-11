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
import java.util.stream.Stream

class HlaFacadeTest {
    data class TestPaths(
        val exampleMainPath: String,
        val exampleTestFixturesPath: String,
        val expectedMainPathSuffix: String,
        val expectedTestFixturesPathSuffix: String
    )
    class MyArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            val kotlinSrcMainPath = "/src/main/kotlin/com/some/pkg"
            val kotlinSrcTestFixturesPath = "/src/testFixtures/kotlin/com/some/pkg"

            val kotlinMainPathPrefix = "../example/kotlin/$kotlinSrcMainPath/"
            val kotlinTestFixturesPrefix = "../example/kotlin/$kotlinSrcTestFixturesPath/"

            return Stream.of(
                Arguments.of(
                    "OtherModule",
                    ModuleLanguage.KOTLIN,
                    TestPaths(
                        exampleMainPath = kotlinMainPathPrefix + "othermodule",
                        exampleTestFixturesPath = kotlinTestFixturesPrefix + "othermodule",
                        expectedMainPathSuffix = kotlinSrcMainPath,
                        expectedTestFixturesPathSuffix = kotlinSrcTestFixturesPath
                    )
                ),
                Arguments.of(
                    "OtherModule",
                    ModuleLanguage.TYPE_SCRIPT,
                    TestPaths(
                        exampleMainPath = "../example/typescript/main/OtherModule",
                        exampleTestFixturesPath = "../example/typescript/test/OtherModule",
                        expectedMainPathSuffix = "/main",
                        expectedTestFixturesPathSuffix = "/test"
                    )
                ),
                Arguments.of(
                    "SomeModule",
                    ModuleLanguage.KOTLIN,
                    TestPaths(
                        exampleMainPath = kotlinMainPathPrefix + "somemodule",
                        exampleTestFixturesPath = kotlinTestFixturesPrefix + "somemodule",
                        expectedMainPathSuffix = kotlinSrcMainPath,
                        expectedTestFixturesPathSuffix = kotlinSrcTestFixturesPath
                    )
                ),
                Arguments.of(
                    "SomeModule",
                    ModuleLanguage.TYPE_SCRIPT,
                    TestPaths(
                        exampleMainPath = "../example/typescript/main/SomeModule",
                        exampleTestFixturesPath = "../example/typescript/test/SomeModule",
                        expectedMainPathSuffix = "/main",
                        expectedTestFixturesPathSuffix = "/test"
                    )
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
            HlaProperties(
                generateWeb = true,
                java = JavaProperties(
                    rootPackage = "com.some.pkg",
                )
            )
        )

        val facade = context.get(HlaFacade::class.java)

        val hlaFolderPath = Path("src/test/resources/facade")
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