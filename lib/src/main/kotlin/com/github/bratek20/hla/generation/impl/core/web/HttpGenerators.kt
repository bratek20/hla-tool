package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.TypeScript
import com.github.bratek20.codebuilder.languages.typescript.namespace
import com.github.bratek20.codebuilder.builders.legacyConst
import com.github.bratek20.codebuilder.builders.legacyReturn
import com.github.bratek20.codebuilder.builders.legacyVariable
import com.github.bratek20.codebuilder.types.type
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.ApiType
import com.github.bratek20.hla.generation.impl.core.api.patterns.InterfaceView
import com.github.bratek20.hla.generation.impl.core.api.patterns.InterfaceViewFactory
import com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinSupport
import com.github.bratek20.hla.generation.impl.languages.typescript.ObjectCreationMapper
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptSupport
import com.github.bratek20.utils.camelToPascalCase
import com.github.bratek20.utils.destringify
import com.github.bratek20.utils.directory.api.FileContent
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
    return web.getHttp()!!.getExposedInterfaces()
        .map { name -> c.module.getInterfaces().first { it.getName() == name } }
        .map { factory.create(it) }
}

class WebCommonGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.WebCommon
    }

    private fun kotlinRequestClass(interfName: String, method: MethodView): ClassBuilderOps {
        return {
            name = requestName(interfName, method)
            constructor {
                method.args.forEach { arg ->
                    addField {
                        accessor = FieldAccessor.PRIVATE
                        name = arg.name
                        type = type(arg.apiType.serializableName())
                    }
                }
            }
            body = {
                method.args.forEach { arg ->
                    legacyMethod {
                        name = "get${camelToPascalCase(arg.name)}"
                        returnType = type(arg.type)
                        legacyBody = {
                            legacyReturn {
                                legacyVariable(arg.apiType.deserialize(arg.name))
                            }
                        }
                    }
                }
            }
            addStaticMethod {
                name = "create"
                returnType = type(requestName(interfName, method))
                method.args.map { arg ->
                    addArg {
                        name = arg.name
                        type = type(arg.type)
                    }
                }
                legacyBody = {
                    legacyReturn {
                        legacyConstructorCall {
                            className = requestName(interfName, method)
                            method.args.forEach {
                                addArgLegacy {
                                    legacyVariable(it.apiType.serialize(it.name))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun objectCreationType(type: ApiType): String  {
        val oc = ObjectCreationMapper()
        return oc.map(type.serializableName())
    }

    private fun typeScriptRequestClass(interfName: String, method: MethodView): ClassBuilderOps {
        return {
            name = requestName(interfName, method)
            body = {
                method.args.forEach { arg ->
                    field {
                        accessor = FieldAccessor.PRIVATE
                        mutable = true
                        name = arg.name
                        legacyValue = {
                            legacyConst(objectCreationType(arg.apiType))
                        }
                    }
                }
                method.args.forEach { arg ->
                    legacyMethod {
                        name = "get${camelToPascalCase(arg.name)}"
                        returnType = type(arg.type)
                        legacyBody = {
                            legacyReturn {
                                legacyVariable(arg.apiType.deserialize("this." + arg.name))
                            }
                        }
                    }
                }
            }
            addStaticMethod {
                name = "create"
                returnType = type(requestName(interfName, method))
                method.args.map { arg ->
                    addArg {
                        name = arg.name
                        type = type(arg.type)
                    }
                }
                legacyBody = {
                    add(variableAssignment {

                        declare = true
                        name = "instance"
                        value = constructorCall {
                            className = requestName(interfName, method)
                        }
                    })
                    method.args.forEach {
                        add(variableAssignment {
                            name = "instance.${it.name}"
                            value = variable(it.apiType.serialize(it.name))
                        })
                    }
                    legacyReturn {
                        legacyVariable("instance")
                    }
                }
            }
        }
    }

    private fun typeScriptResponseClass(interfName: String, method: MethodView): ClassBuilderOps {
        val argName = "value"
        val argType = method.returnType
        val argApiType = method.returnApiType

        return {
            name = responseName(interfName, method)
            body = {
                field {
                    accessor = FieldAccessor.PRIVATE
                    mutable = true
                    name = argName
                    legacyValue = {
                        legacyConst(objectCreationType(argApiType))
                    }
                }

                legacyMethod {
                    name = "get${camelToPascalCase(argName)}"
                    returnType = type(argType)
                    legacyBody = {
                        legacyReturn {
                            legacyVariable(argApiType.deserialize("this.$argName"))
                        }
                    }
                }
            }
        }
    }

    private fun kotlinResponseClass(interfName: String, method: MethodView): ClassBuilderOps {
        return {
            name = responseName(interfName, method)
            constructor {
                addField {
                    name = "value"
                    type = type(method.returnType)
                }
            }
        }
    }

    override fun generateFileContent(): FileContent? {
        if (c.module.getWebSubmodule()?.getHttp() == null) {
            return null
        }

        val exposedInterfaces = exposedInterfaces(c)

        val classes: MutableList<ClassBuilderOps> = mutableListOf()

        exposedInterfaces.forEach { interf ->
            interf.methods.forEach { method ->
                if (method.hasArgs()) {
                    if (c.language is KotlinSupport) {
                        classes.add(kotlinRequestClass(interf.name, method))
                    } else {
                        classes.add(typeScriptRequestClass(interf.name, method))
                    }
                }
                if (method.returnType != "Unit" && method.returnType != "void") {
                    if (c.language is KotlinSupport) {
                        classes.add(kotlinResponseClass(interf.name, method))
                    } else {
                        classes.add(typeScriptResponseClass(interf.name, method))
                    }
                }
            }
        }

        val moduleName = c.module.getName().value
        val view = CodeBuilder(c.language.base())
            .addOps {
                if (c.lang is TypeScript) {
                    namespace {
                        name = "$moduleName.Web"
                        addClass {
                            name = "${moduleName}WebClientConfig"
                            constructor {
                                addField {
                                    name = "value"
                                    type = type("HttpClientConfig")
                                }
                            }
                        }
                        classes.forEach(::addClass)
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

private fun getUrlPathPrefix(c: ModuleGenerationContext): String {
    return c.module.getWebSubmodule()!!.getHttp()!!.getUrlPathPrefix()?.let { destringify(it) } ?: ""
}

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

                            constructor {
                                addArg {
                                    name = "config"
                                    type = type("${moduleName}WebClientConfig")
                                }
                                addArg {
                                    name = "c"
                                    type = type("HandlerContext")
                                }
                                body = {
                                    add(variableAssignment {
                                        name = "this.client"
                                        value = functionCall {
                                            name = "HttpClient.Api.create"
                                            addArg(variable("config.value"))
                                            addArg(variable("c"))
                                        }
                                    })
                                }
                            }
                            body = {
                                field {
                                    accessor = FieldAccessor.PRIVATE
                                    name = "client"
                                    type = type("HttpClient")
                                }
                                interf.methods.forEach { m ->
                                    add(
                                        m.declarationCB().apply {
                                            legacyBody = {
                                                line(getBodyTS(interf.name, m))
                                            }
                                        }
                                    )
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
}

class WebServerGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.WebServer
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

    override fun generateFileContent(): FileContent? {
        if (c.module.getWebSubmodule()?.getHttp() == null) {
            return null
        }
        if (c.language is TypeScriptSupport) {
            return null
        }

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
                    url = "\"${getUrlPathPrefix(c)}/${pascalToCamelCase(interf.name)}\""
                )
            })
            .build()
    }

    private fun getDeclaration(method: com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView): String {
        val returnType = if (method.returnType != "Unit") "Struct" else "Unit"
        val body = if(method.hasArgs())
            "@RequestBody rawRequest: Struct"
        else
            ""
        return "${method.name}($body): $returnType"
    }

    private fun getBody(interfaceName: String, method: com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView): String {
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