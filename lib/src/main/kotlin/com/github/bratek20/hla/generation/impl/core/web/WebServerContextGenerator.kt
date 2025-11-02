package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.types.newListOf
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.submodulePackage

class WebServerContextGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.WebServerContext
    }

    override fun supportsCodeBuilder() = true
    override fun useImportsCalculator() = true
    override fun extraKotlinImports(): List<String> {
        return listOf(
            "com.github.bratek20.architecture.context.api.ContextBuilder",
            "com.github.bratek20.architecture.context.api.ContextModule",
            "com.github.bratek20.infrastructure.httpclient.api.HttpClientConfig",
            "com.github.bratek20.infrastructure.httpserver.api.WebServerModule",
            submodulePackage(moduleGroup, SubmoduleName.Api, c) + ".*",
            submodulePackage(moduleGroup, SubmoduleName.Impl, c) + ".*",
            submodulePackage(moduleGroup, SubmoduleName.Web, c) + ".*",
        )
    }
    override fun shouldGenerate() = c.language.name() == ModuleLanguage.KOTLIN && c.module.getWebSubmodule()?.getHttp() != null

    override fun getOperations(): TopLevelCodeBuilderOps = {
        addClass {
            name = "${moduleName}WebServer"
            extends {
                name = "WebServerModule"
            }

            addMethod {
                name = "apply"
                overridesClassMethod = true
                addArg {
                    name = "builder"
                    type = typeName("ContextBuilder")
                }
                setBody {
                    add(methodCallStatement {
                        target = variable("builder")
                        name = "withModule"
                        addArg {
                            constructorCall {
                                className = "${moduleName}Impl"
                            }
                        }
                    })
                }
            }

            addMethod {
                name = "getControllers"
                overridesClassMethod = true
                returnType = typeName("List<Class<*>>")
                setBody {
                    add(returnStatement {
                        newListOf(
                            typeName("Class<*>"),
                            *c.module.getWebSubmodule()!!.getHttp()!!.getExposedInterfaces().map { interfaceName ->
                                expression {
                                    "${interfaceName}Controller::class.java"
                                }
                            }.toTypedArray()
                        )
                    })
                }
            }
        }
    }
}