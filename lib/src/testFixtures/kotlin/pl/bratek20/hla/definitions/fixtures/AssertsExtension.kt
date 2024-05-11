package pl.bratek20.hla.definitions.fixtures

import pl.bratek20.hla.definitions.api.ModuleDefinition

fun assertModules(given: List<ModuleDefinition>, expected: List<ExpectedModuleDefinition.() -> Unit>) {
    expected.forEachIndexed { idx, expectedModule ->
        assertModuleDefinition(given[idx], expectedModule)
    }
}