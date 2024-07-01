package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.DirectoryGenerator
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.core.api.InterfaceView
import com.github.bratek20.hla.generation.impl.core.api.InterfaceViewFactory
import com.github.bratek20.hla.generation.impl.core.api.MethodView
import com.github.bratek20.hla.utils.camelToPascalCase
import com.github.bratek20.hla.utils.pascalToCamelCase

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

    override fun generateFileContent(): FileContent {
        val exposedInterfaces = exposedInterfaces(c)

        val requests = exposedInterfaces.flatMap { interf ->
            interf.methods.mapNotNull { method ->
                if (!method.hasArgs()) return@mapNotNull null
                "class ${requestName(interf.name, method)}(${method.argsDeclarationWithPrefix("val ")})"
            }
        }

        val responses = exposedInterfaces.flatMap { interf ->
            interf.methods.mapNotNull { method ->
                if (method.returnType == "Unit") return@mapNotNull null
                "class ${responseName(interf.name, method)}(val value: ${method.returnType})"
            }
        }

        return contentBuilder("webCommon.vm")
            .put("requests", requests)
            .put("responses", responses)
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
                "${requestName(interfaceName, method)}(${method.argsPass()})"
            else
                "null"

        return "${returnPart}factory.create(url.value).post($postUrl, $postBody)$getBodyPart"
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
        val apiCall = "api.${method.name}(${method.argsPassWithPrefix("request.")})"
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