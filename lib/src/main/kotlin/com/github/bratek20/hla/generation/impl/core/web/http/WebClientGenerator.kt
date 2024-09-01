package com.github.bratek20.hla.generation.impl.core.web.http

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.languages.typescript.namespace
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.pascalToCamelCase

class WebClientGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.WebClient
    }

    data class MethodView(
        val declaration: String,
        val body: String
    )
    data class InterfaceView(
        val name: String,
        val methods: List<MethodView>
    )

    private fun view(): String {
        val moduleName = c.module.getName().value
        val interfs = exposedInterfaces(c)
        return CodeBuilder(c.language.base())
            .addOps {
                namespace {
                    name = "${moduleName}.Web"
                    interfs.forEach { interf ->
                        addClass {
                            name = "${interf.name}WebClient"
                            implements = interf.name

                            setConstructor {
                                addArg {
                                    name = "config"
                                    type = typeName("${moduleName}WebClientConfig")
                                }
                                addArg {
                                    name = "c"
                                    type = typeName("HandlerContext")
                                }
                                setBody {
                                    add(assignment {
                                        left = expression("this.client")
                                        right = functionCall {
                                            name = "HttpClient.Api.create"
                                            addArg {
                                                variable("config.value")
                                            }
                                            addArg {
                                                variable("c")
                                            }
                                        }
                                    })
                                }
                            }
                            legacyBody = {
                                legacyField {
                                    modifier = AccessModifier.PRIVATE
                                    name = "client"
                                    type = typeName("HttpClient")
                                }
                                interf.methods.forEach { m ->
                                    add(method {
                                        apply(m.declarationCB())
                                        legacyBody = {
                                            line(getBodyTS(interf.name, m))
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }
            .build()
    }

    override fun generateFileContent(): FileContent? {
        if (c.module.getWebSubmodule()?.getHttp() == null) {
            return null
        }

        return contentBuilder("webClient.vm")
            .put("interfaces", exposedInterfaces(c).map { interf ->
                InterfaceView(
                    interf.name,
                    interf.methods.map { method ->
                        MethodView(
                            getDeclaration(method),
                            getBody(interf.name, method)
                        )
                    }
                )
            })
            .put("view", view())
            .build()
    }

    private fun getDeclaration(method: com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView): String {
        return method.declaration()
    }

    private fun getPostUrl(interfaceName: String, method: com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView): String {
        return "\"${getUrlPathPrefix(c)}/${pascalToCamelCase(interfaceName)}/${method.name}\""
    }

    private fun getBody(interfaceName: String, method: com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView): String {
        val returnPart = if (method.returnType != "Unit") "return " else ""
        val getBodyPart = if (method.returnType != "Unit")
            ".getBody(${responseName(interfaceName, method)}::class.java).value"
        else
            ""

        val postUrl = getPostUrl(interfaceName, method)
        val postBody = if(method.hasArgs())
            "${requestName(interfaceName, method)}.create(${method.argsPass()})"
        else
            "null"

        return "${returnPart}client.post($postUrl, $postBody)$getBodyPart"
    }

    private fun getBodyTS(interfaceName: String, method: com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView): String {
        val returnPart = if (method.returnType != "void") "return " else ""
        val getBodyPart = if (method.returnType != "void")
            ".getBody(${responseName(interfaceName, method)}).get().getValue()"
        else
            ""

        val postUrl = getPostUrl(interfaceName, method)
        val postBody = if(method.hasArgs())
            "Optional.of(${requestName(interfaceName, method)}.create(${method.argsPass()}))"
        else
            "Optional.empty()"

        return "${returnPart}this.client.post($postUrl, $postBody)$getBodyPart"
    }

    override fun supportsCodeBuilder() = lang is CSharp
    override fun shouldGenerate() = exposedInterfaces(c).isNotEmpty()
    override fun extraCSharpUsings() = listOf(
        "HttpClientModule.Api"
    )

    override fun getOperations(): TopLevelCodeBuilderOps = {
        exposedInterfaces(c).forEach { interf ->
            addClass {
                name = "${interf.name}WebClient"
                implements = interf.name

                addField {
                    modifier = AccessModifier.PRIVATE
                    name = "client"
                    type = typeName("HttpClient")
                }

                setConstructor {
                    addArg {
                        name = "factory"
                        type = typeName("HttpClientFactory")
                    }
                    addArg {
                        name = "config"
                        type = typeName("${moduleName}WebClientConfig")
                    }
                    setBody {
                        add(assignment {
                            left = expression("this.client")
                            right = functionCall {
                                name = "factory.Create"
                                addArg {
                                    variable("config.Value")
                                }
                            }
                        })
                    }
                }

                interf.methods.forEach { m ->
                    addMethod {
                        apply(m.declarationCB())
                        setBody(getDefaultBody(interf.name, m))
                    }
                }
            }
        }
    }

    private fun getDefaultBody(
        interfaceName: String,
        method: com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView
    ): BodyBuilderOps = {
        val hasReturnValue = method.hasReturnValue()

        val getBodyPart = getterFieldAccess {
            objectRef = optionalOp {
                methodCall {
                    methodName = "getBody"
                    addArg {
                        variable(responseName(interfaceName, method))
                    }
                }
            }.get()
            fieldName = "value"
        }

        val postUrl = getPostUrl(interfaceName, method)
        val reqName = requestName(interfaceName, method)
        val postBody = if(method.hasArgs())
            hardOptional(typeName(reqName)) {
                methodCall {
                    target = variable(reqName)
                    methodName = "create"
                    apply(method.argsPassCB())
                }
            }
        else
            emptyHardOptional(baseType(BaseType.ANY))

        val finalExpression = methodCall {
            target = instanceVariable("client")
            methodName = "post"
            addArg {
                variable(postUrl)
            }
            addArg {
                postBody
            }
        }

        if (hasReturnValue) {
            add(returnStatement {
                expressionChain {
                    finalExpression
                }
                .then {
                    getBodyPart
                }
            })
        } else {
            add(finalExpression.asStatement())
        }
    }
}