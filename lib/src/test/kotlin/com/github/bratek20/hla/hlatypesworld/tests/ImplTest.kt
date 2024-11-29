package com.github.bratek20.hla.hlatypesworld.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldApi
import com.github.bratek20.hla.hlatypesworld.context.HlaTypesWorldImpl
import com.github.bratek20.hla.parsing.api.ModuleGroupParser
import com.github.bratek20.hla.parsing.context.ParsingImpl
import com.github.bratek20.logs.LogsMocks
import com.github.bratek20.utils.directory.api.Path
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HlaTypesWorldImplTest {
    @Test
    fun `should populate types`() {
        val c = someContextBuilder()
            .withModules(
                LogsMocks(),
                ParsingImpl(),
                HlaTypesWorldImpl()
            ).build()

        val parser = c.get(ModuleGroupParser::class.java)
        val world = c.get(HlaTypesWorldApi::class.java)

        val moduleGroup = parser.parse(
            Path("../example/hla"),
            ProfileName("cSharp")
        )

        world.populate(moduleGroup)
    }
}