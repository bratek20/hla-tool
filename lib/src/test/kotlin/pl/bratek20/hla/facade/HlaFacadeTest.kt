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
import pl.bratek20.hla.definitions.api.ModuleName
import pl.bratek20.hla.directory.DirectoriesMock
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.context.DirectoriesMocks
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.facade.api.*
import pl.bratek20.hla.facade.context.FacadeImpl
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.context.GenerationImpl
import pl.bratek20.hla.velocity.context.VelocityImpl
import pl.bratek20.hla.writing.context.WritingImpl
import java.util.stream.Stream

class HlaFacadeTest {
    class MyArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
            val kotlinMainPathPrefix = "../example/kotlin/src/main/kotlin/com/some/path/"
            val kotlinTestFixturesPrefix = "../example/kotlin/src/testFixtures/kotlin/com/some/path/"
            return Stream.of(
                Arguments.of(
                    "OtherModule",
                    kotlinMainPathPrefix + "othermodule",
                    kotlinTestFixturesPrefix + "othermodule",
                    ModuleLanguage.KOTLIN
                ),
                Arguments.of(
                    "OtherModule",
                    "../example/typescript/OtherModule",
                    "../example/typescript/OtherModule",
                    ModuleLanguage.TYPE_SCRIPT
                ),
                Arguments.of(
                    "SomeModule",
                    kotlinMainPathPrefix + "somemodule",
                    kotlinTestFixturesPrefix + "somemodule",
                    ModuleLanguage.KOTLIN
                ),
                Arguments.of(
                    "SomeModule",
                    "../example/typescript/SomeModule",
                    "../example/typescript/SomeModule",
                    ModuleLanguage.TYPE_SCRIPT
                ),
            )
        }
    }

    @ParameterizedTest(name = "{0} ({3})")
    @ArgumentsSource(MyArgumentsProvider::class)
    fun `should generate module`(
        moduleName: String,
        mainPath: String,
        testFixturesPath: String,
        lang: ModuleLanguage
    ) {
        //given
        val context = someContextBuilder()
            .withModules(
                DirectoriesMocks(),

                PropertiesModule(),
                InMemoryPropertiesSourceModule(),

                VelocityImpl(),
                GenerationImpl(),
                WritingImpl(),
                FacadeImpl(),
            )
            .build()

        val directoriesMock = context.get(DirectoriesMock::class.java)
        val propertiesSource = context.get(InMemoryPropertiesSource::class.java)

        propertiesSource.set(
            HLA_PROPERTIES_KEY,
            HlaProperties(
                java = JavaProperties(
                    rootPackage = "pl.bratek20",
                )
            )
        )

        val facade = context.get(HlaFacade::class.java)

        val inPath = Path("src/test/resources/facade")
        val outPath = Path("somePath")

        //when
        facade.generateModule(GenerateModuleArgs(ModuleName(moduleName), lang, inPath, outPath))

        //then
        directoriesMock.assertWriteCount(2)
        val mainDirectory = directoriesMock.getWrittenDirectory(1)
        val testFixturesDirectory = directoriesMock.getWrittenDirectory(2)

        assertWrittenDirectoryWithExample(mainDirectory, mainPath)
        assertWrittenDirectoryWithExample(testFixturesDirectory, testFixturesPath)
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