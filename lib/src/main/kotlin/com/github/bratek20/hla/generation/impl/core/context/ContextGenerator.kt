package com.github.bratek20.hla.generation.impl.core.context

import com.github.bratek20.codebuilder.builders.constructorCall
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.typescript.namespace
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.generation.impl.core.DirectoryGenerator
import com.github.bratek20.hla.generation.impl.core.FileGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.api.InterfaceViewFactory

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
                .put("view", view())
                .build()
        }
    }

    private fun view(): String {
//        namespace SomeModule.Api {
//            const config = new SomeModuleWebClientConfig(
//                new HttpClientConfig(
//                        EnvVars.get("BASE_URL"),
//                new HttpClientAuth(
//                        EnvVars.get("USERNAME"),
//                EnvVars.get("PASSWORD"),
//            )
//            )
//            )
//
//            export function someEmptyMethod(c: HandlerContext): void {
//            new Web.SomeInterfaceWebClient(c).someEmptyMethod()
//        }
//
//            export function someCommand(id: SomeId, amount: number, c: HandlerContext): void {
//            new Impl.SomeInterfaceLogic(c).someCommand(id, amount)
//        }
//
//            export function someQuery(query: SomeQueryInput, c: HandlerContext): SomeClass {
//            return new Impl.SomeInterfaceLogic(c).someQuery(query)
//        }
//
//            export function optMethod(optId: Optional<SomeId>, c: HandlerContext): Optional<SomeClass> {
//            return new Impl.SomeInterfaceLogic(c).optMethod(optId)
//        }
//
//            export function referenceOtherClass(other: OtherClass, c: HandlerContext): OtherClass {
//            return new Impl.SomeInterface2Logic(c).referenceOtherClass(other)
//        }
//
//            export function referenceLegacyType(legacyType: LegacyType, c: HandlerContext): LegacyType {
//            return new Impl.SomeInterface2Logic(c).referenceLegacyType(legacyType)
//        }
//
//            export function referenceInterface(empty: SomeEmptyInterface, c: HandlerContext): SomeEmptyInterface {
//            return new Impl.SomeInterface3Logic(c).referenceInterface(empty)
//        }
//
//            export function referenceOtherInterface(other: OtherInterface, c: HandlerContext): OtherInterface {
//            return new Impl.SomeInterface3Logic(c).referenceOtherInterface(other)
//        }
//        }
        return CodeBuilder(c.language.base())
            .add {
                namespace {
                    name = "${this@WebContextGenerator.c.module.getName()}.Api"
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