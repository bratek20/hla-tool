package com.github.bratek20.hla.generation.impl.core.context

import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.types.type
import com.github.bratek20.codebuilder.typescript.namespace
import com.github.bratek20.hla.generation.impl.core.DirectoryGenerator
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.api.InterfaceViewFactory
import com.github.bratek20.utils.directory.api.FileContent

class ImplContextGenerator: FileGenerator() {
    override fun name(): String {
        return "Impl"
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

class WebContextGenerator: FileGenerator() {
    override fun name(): String {
        return "Web"
    }

    override fun generateFileContent(): FileContent? {
        return c.module.getWebSubmodule()?.let { web ->
            contentBuilder("web.vm")
                .put("serverUrl", web.getServerUrl() ?: "\"http://localhost:8080\"")
                .put("interfaceNames", web.getExpose())
                .put("view", view(web.getExpose()))
                .build()
        }
    }

    private fun view(interfNames: List<String>): String {
        val factory = InterfaceViewFactory(apiTypeFactory)
        val interfDefs = module.getInterfaces().filter { interfNames.contains(it.getName()) }
        val interfs = factory.create(interfDefs)
        return CodeBuilder(c.language.base())
            .add {
                namespace {
                    name = "${this@WebContextGenerator.c.module.getName()}.Api"
                    interfs.forEach { interf ->
                        interf.methods.forEach { m ->
                            function {
                                name = m.name
                                returnType = type(m.returnType)
                                m.args.forEach { arg ->
                                    addArg {
                                        name = arg.name
                                        type = type(arg.type)
                                    }
                                }
                                addArg {
                                    name = "c"
                                    type = type("HandlerContext")
                                }
                                body = {
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

class ContextGenerator: DirectoryGenerator() {
    override fun name(): String {
        return "Context"
    }

    override fun velocityDirPath(): String {
        return "context"
    }

    override fun shouldGenerateDirectory(): Boolean {
        return module.getInterfaces().isNotEmpty()
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            ImplContextGenerator(),
            WebContextGenerator()
        )
    }
}