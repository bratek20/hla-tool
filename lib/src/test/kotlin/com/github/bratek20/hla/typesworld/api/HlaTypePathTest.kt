package com.github.bratek20.hla.typesworld.api

import ch.qos.logback.classic.PatternLayout
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.facade.fixtures.assertHlaPaths
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.typesworld.fixtures.assertHlaTypePath
import com.github.bratek20.hla.typesworld.fixtures.hlaTypePath
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HlaTypePathTest {
    @Test
    fun shouldWork() {
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