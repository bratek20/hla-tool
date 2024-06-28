package com.github.bratek20.hla.queries

import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.parsing.fixtures.moduleGroup
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.hla.queries.fixtures.assertModuleDependencies
import org.junit.jupiter.api.Test

class ModuleGroupQueriesTest {
    @Test
    fun shouldCalculateDependencies() {
        // given
        val group = moduleGroup {
            modules = listOf(
                {
                    name = "A"
                    simpleValueObjects = listOf {
                        name = "AClass"
                    }
                },
                {
                    name = "B"
                    complexValueObjects = listOf {
                        name = "BClass"
                        fields = listOf {
                            name = "field"
                            type = {
                                name = "AClass"
                            }
                        }
                    }
                }
            )
        }

        // when
        val hlaModules = ModuleGroupQueries(ModuleName("B"), group)

        // then
        assertModuleDependencies(hlaModules.getCurrentDependencies(), listOf {
            module = {
                name = "A"
            }
        })
    }

    @Test
    fun shouldCalculateDependenciesIncludingInterfaces() {
        // given
        val group = moduleGroup {
            modules = listOf(
                {
                    name = "A"
                    simpleValueObjects = listOf {
                        name = "AClass"
                    }
                },
                {
                    name = "B"
                    simpleValueObjects = listOf {
                        name = "BClass"
                    }
                    interfaces = listOf {
                        name = "BInterface"
                    }
                },
                {
                    name = "C"
                    interfaces = listOf {
                        name = "CInterface"
                        methods = listOf {
                            args = listOf {
                                type = {
                                    name = "AClass"
                                }
                            }
                            returnType = {
                                name = "BClass"
                            }
                        }
                    }
                },
                {
                    name = "D"
                    interfaces = listOf {
                        methods = listOf {
                            args = listOf {
                                type = {
                                    name = "CInterface"
                                }
                            }
                            returnType = {
                                name = "BInterface"
                            }
                        }
                    }
                }
            )
        }

        // when
        val hlaModulesForC = ModuleGroupQueries(ModuleName("C"), group)
        val hlaModulesForD = ModuleGroupQueries(ModuleName("D"), group)

        // then
        assertModuleDependencies(hlaModulesForC.getCurrentDependencies(), listOf(
            {
                module = {
                    name = "A"
                }
            },
            {
                module = {
                    name = "B"
                }
            }
        ))

        assertModuleDependencies(hlaModulesForD.getCurrentDependencies(), listOf(
            {
                module = {
                    name = "B"
                }
            },
            {
                module = {
                    name = "C"
                }
            }
        ))
    }
}