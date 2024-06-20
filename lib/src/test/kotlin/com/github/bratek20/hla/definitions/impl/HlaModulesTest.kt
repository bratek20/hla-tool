package com.github.bratek20.hla.definitions.impl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import com.github.bratek20.hla.definitions.fixtures.moduleDefinition
import com.github.bratek20.hla.facade.api.ModuleName

class HlaModulesTest {
    @Test
    fun shouldCalculateDependencies() {
        // given
        val modules = listOf(
           moduleDefinition {
               name = "A"
               simpleValueObjects =  listOf {
                   name = "AClass"
               }
           },
           moduleDefinition {
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

        // when
        val hlaModules = HlaModules(ModuleName("B"), modules)

        // then
        assertThat(hlaModules.getCurrentDependencies())
            .containsExactly(ModuleName("A"))
    }

    @Test
    fun shouldCalculateDependenciesIncludingInterfaces() {
        // given
        val modules = listOf(
            moduleDefinition {
                name = "A"
                simpleValueObjects =  listOf {
                    name = "AClass"
                }
            },
            moduleDefinition {
                name = "B"
                simpleValueObjects =  listOf {
                    name = "BClass"
                }
                interfaces = listOf {
                    name = "BInterface"
                }
            },
            moduleDefinition {
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
            moduleDefinition {
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

        // when
        val hlaModulesForC = HlaModules(ModuleName("C"), modules)
        val hlaModulesForD = HlaModules(ModuleName("D"), modules)

        // then
        assertThat(hlaModulesForC.getCurrentDependencies())
            .containsExactly(ModuleName("A"), ModuleName("B"))
        assertThat(hlaModulesForD.getCurrentDependencies())
            .containsExactly(ModuleName("B"), ModuleName("C"))
    }
}