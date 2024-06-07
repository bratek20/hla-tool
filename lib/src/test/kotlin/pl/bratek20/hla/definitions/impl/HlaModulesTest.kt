package pl.bratek20.hla.definitions.impl

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import pl.bratek20.hla.definitions.fixtures.moduleDefinition
import pl.bratek20.hla.facade.api.ModuleName

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
            },
            moduleDefinition {
                name = "C"
                interfaces = listOf {
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
            }
        )

        // when
        val hlaModules = HlaModules(ModuleName("C"), modules)

        // then
        assertThat(hlaModules.getCurrentDependencies())
            .containsExactly(ModuleName("A"), ModuleName("B"))
    }
}