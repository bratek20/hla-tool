package com.github.bratek20.hla.generation.impl.core.context

import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.codebuilder.languages.typescript.namespace
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.api.patterns.InterfaceViewFactory
import com.github.bratek20.utils.directory.api.FileContent

class ImplContextGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Impl
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun generateFileContent(): FileContent {
        val factory = InterfaceViewFactory(apiTypeFactory)
        return contentBuilder("impl.vm")
            .put("interfaces", factory.create(module.getInterfaces()))
            .build()
    }
}

class WebContextGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Web
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
                namespace {
                    name = "${this@WebContextGenerator.c.module.getName()}.Api"
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
                }
            }
            .build()
    }
}

class ContextGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Context
    }

    override fun velocityDirPath(): String {
        return "context"
    }

    override fun shouldGenerateSubmodule(): Boolean {
        return module.getInterfaces().isNotEmpty()
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            ImplContextGenerator(),
            WebContextGenerator()
        )
    }
}