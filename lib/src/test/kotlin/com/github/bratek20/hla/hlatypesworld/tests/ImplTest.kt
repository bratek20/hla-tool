package com.github.bratek20.hla.hlatypesworld.tests

import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.fixtures.assertHlaTypePath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HlaTypesWorldImplTest {
    @Test
    fun replaceSubmoduleAndPattern() {
        val path = HlaTypePath.create(
            ModuleName("Module"),
            SubmoduleName.Api,
            PatternName.ValueObjects
        )

        path.replaceSubmoduleAndPattern(SubmoduleName.Impl, PatternName.Logic).let {
            assertHlaTypePath(it, "Module/Impl/Logic")
        }
    }
}