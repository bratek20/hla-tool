package com.github.bratek20.hla.queries.fixtures

import com.github.bratek20.hla.queries.api.ModuleDependency
import org.assertj.core.api.Assertions.assertThat

fun assertModuleDependencies(given: List<ModuleDependency>, expected: List<ExpectedModuleDependency.() -> Unit>) {
    assertThat(given).hasSameSizeAs(expected)
    given.forEachIndexed { index, moduleDependency ->
        assertModuleDependency(moduleDependency, expected[index])
    }
}