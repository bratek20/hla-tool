package com.github.bratek20.hla.definitions.fixtures

import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.parsing.fixtures.ExpectedModuleGroup
import com.github.bratek20.hla.parsing.fixtures.assertModuleGroup
import org.assertj.core.api.Assertions.assertThat

fun assertModules(given: List<ModuleDefinition>, expected: List<ExpectedModuleDefinition.() -> Unit>) {
    assertThat(given).hasSameSizeAs(expected)
    expected.forEachIndexed { idx, expectedModule ->
        assertModuleDefinition(given[idx], expectedModule)
    }
}