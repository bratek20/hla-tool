package pl.bratek20.hla.facade

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import pl.bratek20.architecture.context.someContextBuilder
import pl.bratek20.architecture.properties.PropertiesMock
import pl.bratek20.architecture.properties.PropertiesMockContextModule
import pl.bratek20.architecture.properties.api.PropertiesSourceName
import pl.bratek20.architecture.properties.api.PropertyKey
import pl.bratek20.hla.definitions.api.ModuleName
import pl.bratek20.hla.directory.DirectoriesMock
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.context.DirectoriesMocks
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.facade.api.GenerateModuleArgs
import pl.bratek20.hla.facade.api.HlaFacade
import pl.bratek20.hla.facade.api.HlaProperties
import pl.bratek20.hla.facade.api.JavaProperties
import pl.bratek20.hla.facade.context.FacadeImpl
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.context.GenerationImpl
import pl.bratek20.hla.velocity.context.VelocityImpl
import java.util.stream.Stream

class HlaFacadeTest {
    class MyArgumentsProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> =
            Stream.of(
                Arguments.of(
                    "OtherModule",
                    "../example/kotlin/src/main/kotlin/pl/bratek20/othermodule",
                    ModuleLanguage.KOTLIN
                ),
                Arguments.of(
                    "OtherModule",
                    "../example/typescript/OtherModule",
                    ModuleLanguage.TYPE_SCRIPT
                ),
                Arguments.of(
                    "SomeModule",
                    "../example/kotlin/src/main/kotlin/pl/bratek20/somemodule",
                    ModuleLanguage.KOTLIN
                ),
                Arguments.of(
                    "SomeModule",
                    "../example/typescript/SomeModule",
                    ModuleLanguage.TYPE_SCRIPT
                ),
            )
    }

    @ParameterizedTest(name = "{0} ({2})")
    @ArgumentsSource(MyArgumentsProvider::class)
    fun `should generate module`(moduleName: String, path: String, lang: ModuleLanguage) {
        //given
        val context = someContextBuilder()
            .withModules(
                DirectoriesMocks(),
                PropertiesMockContextModule(),
                VelocityImpl(),
                GenerationImpl(),
                FacadeImpl()
            )
            .build()

        val directoriesMock = context.get(DirectoriesMock::class.java)
        val propertiesMock = context.get(PropertiesMock::class.java)

        propertiesMock.setProperty(
            PropertiesSourceName("files"),
            PropertyKey("properties"),
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
        val writtenDirectory = directoriesMock.assertOneWriteAndGetDirectory(outPath.value)
        assertWrittenDirectoryWithExample(writtenDirectory, path)
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