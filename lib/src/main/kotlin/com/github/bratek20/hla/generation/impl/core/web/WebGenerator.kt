package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.codebuilder.core.Kotlin
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.ops.returnBlock
import com.github.bratek20.codebuilder.ops.variable
import com.github.bratek20.codebuilder.types.type
import com.github.bratek20.codebuilder.typescript.namespace
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.DirectoryGenerator
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.core.api.InterfaceView
import com.github.bratek20.hla.generation.impl.core.api.InterfaceViewFactory
import com.github.bratek20.hla.generation.impl.core.api.MethodView
import com.github.bratek20.utils.camelToPascalCase
import com.github.bratek20.utils.pascalToCamelCase

private fun requestName(interfaceName: String, method: MethodView): String {
    return "${interfaceName}${camelToPascalCase(method.name)}Request"
}

private fun responseName(interfaceName: String, method: MethodView): String {
    return "${interfaceName}${camelToPascalCase(method.name)}Response"
}

private fun exposedInterfaces(c: ModuleGenerationContext): List<InterfaceView> {
    val factory = InterfaceViewFactory(c.apiTypeFactory)

    val web = c.module.getWebSubmodule()!!
    return web.getExpose()
        .map { name -> c.module.getInterfaces().first { it.getName() == name } }
        .map { factory.create(it) }
}

class WebCommonGenerator: FileGenerator() {
    override fun name(): String {
        return "WebCommon"
    }

    private fun requestClass(interfName: String, method: MethodView): ClassBuilderOps {
        return {
            name = requestName(interfName, method)
            method.args.forEach { arg ->
                constructorField {
                    accessor = FieldAccessor.PRIVATE
                    name = arg.name
                    type = type(arg.apiType.serializableName())
                }
            }
            body = {
                method.args.forEach { arg ->
                    method {
                        name = "get${camelToPascalCase(arg.name)}"
                        returnType = type(arg.type)
                        body = {
                            returnBlock {
                                variable(arg.apiType.deserialize(arg.name))
                            }
                        }
                    }
                }
            }
            staticMethod {
                name = "create"
                returnType = type(requestName(interfName, method))
                method.args.map { arg ->
                   addArg {
                       name = arg.name
                       type = type(arg.type)
                   }
                }
                body = {
                    returnBlock {
                        constructorCall {
                            className = requestName(interfName, method)
                            method.args.forEach {
                                addArg {
                                    variable(it.apiType.serialize(it.name))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun responseClass(interfName: String, method: MethodView): ClassBuilderOps {
        return {
            name = responseName(interfName, method)
            constructorField {
                name = "value"
                type = type(method.returnType)
            }
        }
    }
    override fun generateFileContent(): FileContent {
        val exposedInterfaces = exposedInterfaces(c)

        val classes: MutableList<ClassBuilderOps> = mutableListOf()

        exposedInterfaces.forEach { interf ->
            interf.methods.forEach { method ->
                if (method.hasArgs()) {
                    classes.add(requestClass(interf.name, method))
                }
                if (method.returnType != "Unit") {
                    classes.add(responseClass(interf.name, method))
                }
            }
        }

        val view = CodeBuilder(c.language.base())
            .add {
                if (c.lang is TypeScript) {
                    namespace {
                        name = "Some"
                        classes.forEach(::classBlock)
                    }
                } else {
                    classes.forEach(::classBlock)
                }
            }
            .build()
        return contentBuilder("webCommon.vm")
            .put("view", view)
            .build()
    }
}

class WebClientGenerator: FileGenerator() {
    override fun name(): String {
        return "WebClient"
    }

    data class MethodView(
        val declaration: String,
        val body: String
    )
    data class InterfaceView(
        val name: String,
        val methods: List<MethodView>
    )

    override fun generateFileContent(): FileContent {
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
            .build()
    }

    private fun getDeclaration(method: com.github.bratek20.hla.generation.impl.core.api.MethodView): String {
        return method.declaration()
    }

    private fun getBody(interfaceName: String, method: com.github.bratek20.hla.generation.impl.core.api.MethodView): String {
        val returnPart = if (method.returnType != "Unit") "return " else ""
        val getBodyPart = if (method.returnType != "Unit")
                ".getBody(${responseName(interfaceName, method)}::class.java).value"
            else
                ""

        val postUrl = "\"/${pascalToCamelCase(interfaceName)}/${method.name}\""
        val postBody = if(method.hasArgs())
                "${requestName(interfaceName, method)}.create(${method.argsPass()})"
            else
                "null"

        return "${returnPart}client.post($postUrl, $postBody)$getBodyPart"
    }
}

class WebServerGenerator: FileGenerator() {
    override fun name(): String {
        return "WebServer"
    }

    data class MethodView(
        val declaration: String,
        val body: String,
        val url: String,
    )
    data class InterfaceView(
        val name: String,
        val methods: List<MethodView>,
        val url: String,
    )

    override fun generateFileContent(): FileContent {
        return contentBuilder("webServer.vm")
            .put("interfaces", exposedInterfaces(c).map { interf ->
                InterfaceView(
                    name = interf.name,
                    methods = interf.methods.map { method ->
                        MethodView(
                            getDeclaration(method),
                            getBody(interf.name, method),
                            url = "\"/${method.name}\""
                        )
                    },
                    url = "\"/${pascalToCamelCase(interf.name)}\""
                )
            })
            .build()
    }

    private fun getDeclaration(method: com.github.bratek20.hla.generation.impl.core.api.MethodView): String {
        val returnType = if (method.returnType != "Unit") "Struct" else "Unit"
        val body = if(method.hasArgs())
            "@RequestBody rawRequest: Struct"
        else
            ""
        return "${method.name}($body): $returnType"
    }

    private fun getBody(interfaceName: String, method: com.github.bratek20.hla.generation.impl.core.api.MethodView): String {
        val firstLine = if (method.hasArgs()) "val request = serializer.fromStruct(rawRequest, ${requestName(interfaceName, method)}::class.java)" else "// no request needed"

        val prefix = if (method.returnType != "Unit") "return serializer.asStruct(${responseName(interfaceName, method)}(" else ""
        val apiCall = "api.${method.name}(${method.argsGetPassWithPrefix("request.")})"
        val suffix = if (method.returnType != "Unit") "))" else ""
        val secondLine = "${prefix}${apiCall}${suffix}"

        val secondLineIndent = "        "

        return "$firstLine\n" +
                "$secondLineIndent$secondLine"

    }
}

class WebGenerator: DirectoryGenerator() {
    override fun name(): String {
        return "Web"
    }

    override fun velocityDirPath(): String {
        return "web"
    }

    override fun shouldGenerateDirectory(): Boolean {
        return c.module.getWebSubmodule() != null
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            WebCommonGenerator(),
            WebClientGenerator(),
            WebServerGenerator()
        )
    }
}