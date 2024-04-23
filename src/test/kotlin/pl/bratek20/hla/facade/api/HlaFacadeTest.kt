package pl.bratek20.hla.facade.api

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import pl.bratek20.hla.directory.DirectoriesMock
import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoryLogic
import pl.bratek20.hla.facade.impl.HlaFacadeImpl
import pl.bratek20.hla.generation.api.ModuleLanguage
import pl.bratek20.hla.generation.api.ModuleName
import java.util.stream.Stream

class HlaFacadeTest {
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

    @Disabled // TODO: enable when parsing works
    @ParameterizedTest(name = "{0} ({2})")
    @ArgumentsSource(MyArgumentsProvider::class)
    fun `should generate module (E2E)`(moduleName: String, path: String, lang: ModuleLanguage) {
        //given
        val directoriesMock = DirectoriesMock()
        val facade = HlaFacadeImpl(directoriesMock)
        val inPath = Path("src/test/resources/facade")
        val outPath = Path("somePath")

        //when
        facade.generateModule(GeneratedModuleArgs(ModuleName(moduleName), lang, inPath, outPath))

        //then
        val writtenDirectory = directoriesMock.assertOneWriteAndGetDirectory(outPath.value)
        assertWrittenDirectoryWithExample(writtenDirectory, path)
    }


    private fun assertWrittenDirectoryWithExample(writtenDirectory: Directory, examplePath: String ) {
        val directories = DirectoryLogic()
        val exampleDirectory = directories.readDirectory(Path(examplePath))

        val compareResult = directories.compare(writtenDirectory, exampleDirectory)
        val failMessage = "${compareResult.differences.size} differences found!\n" +
                compareResult.differences.joinToString("\n")

        Assertions.assertThat(compareResult.same)
            .withFailMessage(failMessage)
            .isTrue()
    }
}