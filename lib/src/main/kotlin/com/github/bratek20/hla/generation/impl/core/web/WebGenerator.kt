package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.hla.definitions.api.MethodDefinition
import com.github.bratek20.hla.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.DirectoryGenerator
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.api.InterfaceViewFactory
import com.github.bratek20.hla.generation.impl.core.api.MethodView
import com.github.bratek20.hla.utils.camelToPascalCase
import com.github.bratek20.hla.utils.pascalToCamelCase

class WebCommonGenerator: FileGenerator() {
    override fun name(): String {
        return "WebCommon"
    }

    override fun generateFileContent(): FileContent? {
        return contentBuilder("webCommon.vm")
            .build()
    }
}

fun requestName(interfaceName: String, method: MethodView): String {
    return "${interfaceName}${camelToPascalCase(method.name)}Request"
}

fun responseName(interfaceName: String, method: MethodView): String {
    return "${interfaceName}${camelToPascalCase(method.name)}Response"
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
        val web = c.module.getWebSubmodule()!!
        val exposedInterfaces = web.getExpose()
            .map { name -> c.module.getInterfaces().first { it.getName() == name } }

        return contentBuilder("webClient.vm")
            .put("interface", exposedInterfaces.map { interf ->
                val factory = InterfaceViewFactory(apiTypeFactory)
                val api = factory.create(interf)
                InterfaceView(
                    interf.getName(),
                    api.methods.map { method ->
                        MethodView(
                            getDeclaration(method),
                            getBody(interf.getName(), method)
                        )
                    }
                )
            }[0])
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
        val postBody = "${requestName(interfaceName, method)}(${method.argsPass()})"
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
        val web = c.module.getWebSubmodule()!!
        val exposedInterfaces = web.getExpose()
            .map { name -> c.module.getInterfaces().first { it.getName() == name } }

        return contentBuilder("webServer.vm")
            .put("interface", exposedInterfaces.map { interf ->
                val factory = InterfaceViewFactory(apiTypeFactory)
                val api = factory.create(interf)
                InterfaceView(
                    name = interf.getName(),
                    methods = api.methods.map { method ->
                        MethodView(
                            getDeclaration(interf.getName(), method),
                            getBody(interf.getName(), method),
                            url = "\"/${method.name}\""
                        )
                    },
                    url = "\"/${pascalToCamelCase(interf.getName())}\""
                )
            }[0])
            .build()
    }

    private fun getDeclaration(interfaceName: String, method: com.github.bratek20.hla.generation.impl.core.api.MethodView): String {
        val returnType = if (method.returnType != "Unit") responseName(interfaceName, method) else "Unit"
        return "${method.name}(@RequestBody request: ${requestName(interfaceName, method)}): $returnType"
    }

    private fun getBody(interfaceName: String, method: com.github.bratek20.hla.generation.impl.core.api.MethodView): String {
        val prefix = if (method.returnType != "Unit") "return ${responseName(interfaceName, method)}(" else ""
        val suffix = if (method.returnType != "Unit") ")" else ""
        return "${prefix}api.${method.name}(${method.argsPassWithPrefix("request.")})${suffix}"
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