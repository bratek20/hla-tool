package com.github.bratek20.hla.queries

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import com.github.bratek20.hla.definitions.fixtures.moduleDefinition
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.parsing.fixtures.moduleGroup

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
        assertThat(hlaModules.getCurrentDependencies())
            .containsExactly(ModuleName("A"))
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
        assertThat(hlaModulesForC.getCurrentDependencies())
            .containsExactly(ModuleName("A"), ModuleName("B"))
        assertThat(hlaModulesForD.getCurrentDependencies())
            .containsExactly(ModuleName("B"), ModuleName("C"))
    }
}