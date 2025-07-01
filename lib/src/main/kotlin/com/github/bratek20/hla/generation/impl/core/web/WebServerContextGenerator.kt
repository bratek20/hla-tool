package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.languages.typescript.typeScriptNamespace
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.patterns.InterfaceViewFactory
import com.github.bratek20.utils.directory.api.FileContent

class WebServerContextGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.WebServerContext
    }

    override fun generateFileContent(): FileContent? {
        if (c.module.getWebSubmodule()?.getHttp() == null) {
            return null
        }
        return c.module.getWebSubmodule()?.let { web ->
            contentBuilder("web.vm")
                .put("serverUrl", "\"http://localhost:8080\"")
                .put("serverName", web.getHttp()!!.getServerName())
                .put("baseUrl", web.getHttp()!!.getBaseUrl())
                .put("auth", web.getHttp()!!.getAuth())
                .put("interfaceNames", web.getHttp()!!.getExposedInterfaces())
                .put("view", view(web.getHttp()!!.getExposedInterfaces()))
                .build()
        }
    }

    private fun view(interfNames: List<String>): String {
        val factory = InterfaceViewFactory(apiTypeFactory)
        val interfDefs = module.getInterfaces().filter { interfNames.contains(it.getName()) }
        val interfs = factory.create(interfDefs)
        return CodeBuilder(c.language.base())
            .addOps {
                add(typeScriptNamespace {
                    name = "${this@WebServerContextGenerator.c.module.getName()}.Api"
                    interfs.forEach { interf ->
                        interf.methods.forEach { m ->
                            addFunction {
                                name = m.name
                                returnType = typeName(m.returnType)
                                m.args.forEach { arg ->
                                    addArg {
                                        name = arg.name
                                        type = typeName(arg.type)
                                    }
                                }
                                addArg {
                                    name = "c"
                                    type = typeName("HandlerContext")
                                }
                                legacyBody = {
                                    val returnPart = if (m.returnType != "void") "return " else ""
                                    line("${returnPart}new Web.${interf.name}WebClient(Web.config, c).${m.name}(${m.argsPass()})")
                                }
                            }
                        }
                    }
                })
            }
            .build()
    }

    override fun supportsCodeBuilder(): Boolean {
        return language.name() == ModuleLanguage.C_SHARP
    }

    override fun shouldGenerate(): Boolean {
        return module.getWebSubmodule()?.getHttp() != null
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        addClass {
            name = "${module.getName()}WebClient"
            implements = "ContextModule"

            addField {
                name = "config"
                type = typeName("HttpClientConfig")
                fromConstructor = true
            }

            addMethod {
                name = "apply"
                addArg {
                    name = "builder"
                    type = typeName("ContextBuilder")
                }

                setBody {
                    val builderOperations = expressionChainStatement {
                        instanceVariable("builder")
                    }.then {
                        methodCall {
                            methodName = "setImplObject"
                            addGeneric("${module.getName()}WebClientConfig")
                            addArg {
                                constructorCall {
                                    className = "${module.getName()}WebClientConfig"
                                    addArg {
                                        variable("config")
                                    }
                                }
                            }
                        }
                    }

                    module.getWebSubmodule()!!.getHttp()!!.getExposedInterfaces().forEach { interf ->
                        builderOperations.then {
                            methodCall {
                                methodName = "setImpl"
                                addGeneric(interf)
                                addGeneric("${interf}WebClient")
                            }
                        }
                    }

                    add(builderOperations)
                }
            }
        }
    }

    override fun extraCSharpUsings(): List<String> = listOf(
        "B20.Architecture.Contexts.Api",
        "HttpClientModule.Api"
    )

}