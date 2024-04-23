package pl.bratek20.hla.parsing.api

import org.junit.jupiter.api.Test
import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.model.assertModules
import pl.bratek20.hla.parsing.impl.HlaModulesParserImpl

class HlaModulesParserTest {
    private val parser = HlaModulesParserImpl()

    @Test
    fun `should parse modules`() {
        val modules = parser.parse(Path("src/test/resources/parsing"))

        assertModules(modules, listOf(
            { name = "OtherModule" },
            { name = "SomeModule" }
        ))
    }

}