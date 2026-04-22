package com.github.bratek20.hla.generation.impl.core.web.http

import com.github.bratek20.codebuilder.builders.hardcodedExpression
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptSupport
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.pascalToCamelCase

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
        val requestResponseWrapping = c.module.getWebSubmodule()?.getHttp()?.getRequestResponseWrapping() ?: true

        val returnType = if (method.returnType != "Unit") "Struct" else "Unit"
        val body = if(method.hasArgs()) {
            if (requestResponseWrapping) {
                "@RequestBody rawRequest: Struct"
            } else {
                "@RequestBody rawRequest: Struct"
            }
        } else {
            ""
        }
        return "${method.name}($body): $returnType"
    }

    private fun getBody(interfaceName: String, method: com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView): String {
        val requestResponseWrapping = c.module.getWebSubmodule()?.getHttp()?.getRequestResponseWrapping() ?: true

        val firstLine = if (method.hasArgs()) {
            if (requestResponseWrapping) {
                "val request = serializer.fromStruct(rawRequest, ${requestName(interfaceName, method)}::class.java)"
            } else {
                "val request = serializer.fromStruct(rawRequest, ${method.args.first().type}::class.java)"
            }
        } else {
            "// no request needed"
        }

        val initApiCall = if (requestResponseWrapping) {
            "api.${method.name}(${method.argsGetPassWithPrefix("request.")})"
        } else {
            "api.${method.name}(request)"
        }

        val secondLine = if (method.returnType != "Unit") {
            val serializedCall = if (requestResponseWrapping) {
                method.returnApiType.modernSerialize(hardcodedExpression(initApiCall)).build(c.language.types().context())
            } else {
                initApiCall
            }
            val wrappedCall = if (requestResponseWrapping) {
                "${responseName(interfaceName, method)}($serializedCall)"
            } else {
                serializedCall
            }
            "return serializer.asStruct($wrappedCall)"
        } else {
            initApiCall
        }

        val secondLineIndent = "        "

        return "$firstLine\n" +
                "$secondLineIndent$secondLine"

    }
}