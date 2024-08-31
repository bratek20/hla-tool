package com.github.bratek20.hla.generation.impl.core.web.http

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.languages.typescript.namespace
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.ApiType
import com.github.bratek20.hla.generation.impl.core.api.BaseApiType
import com.github.bratek20.hla.generation.impl.core.api.patterns.InterfaceView
import com.github.bratek20.hla.generation.impl.core.api.patterns.InterfaceViewFactory
import com.github.bratek20.hla.generation.impl.core.api.patterns.MethodView
import com.github.bratek20.hla.generation.impl.languages.typescript.ObjectCreationMapper
import com.github.bratek20.utils.camelToPascalCase
import com.github.bratek20.utils.destringify
import com.github.bratek20.utils.directory.api.FileContent

fun requestName(interfaceName: String, method: MethodView): String {
    return "${interfaceName}${camelToPascalCase(method.name)}Request"
}

fun responseName(interfaceName: String, method: MethodView): String {
    return "${interfaceName}${camelToPascalCase(method.name)}Response"
}

fun exposedInterfaces(c: ModuleGenerationContext): List<InterfaceView> {
    val factory = InterfaceViewFactory(c.apiTypeFactory)

    return c.module.getWebSubmodule()?.getHttp()?.getExposedInterfaces()
        ?.map { name -> c.module.getInterfaces().first { it.getName() == name } }
        ?.map { factory.create(it) } ?: emptyList()
}

fun getUrlPathPrefix(c: ModuleGenerationContext): String {
    return c.module.getWebSubmodule()?.getHttp()?.getUrlPathPrefix()?.let { destringify(it) } ?: ""
}

class WebCommonGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.WebCommon
    }

    private fun defaultRequestClass(interfName: String, method: MethodView): ClassBuilderOps {
        return {
            name = requestName(interfName, method)

            method.args.forEach { arg ->
                addField {
                    modifier = AccessModifier.PRIVATE
                    name = arg.name
                    type = arg.apiType.serializableBuilder()
                    fromConstructor = true
                }
            }


            method.args.forEach { arg ->
                addMethod {
                    name = "get${camelToPascalCase(arg.name)}"
                    returnType = arg.apiType.builder()
                    setBody {
                        add(returnStatement {
                            arg.apiType.modernDeserialize(arg.name)
                        })
                    }
                }
            }

            addMethod {
                static = true
                name = "create"
                returnType = typeName(requestName(interfName, method))
                method.args.map { arg ->
                    addArg {
                        name = arg.name
                        type = arg.apiType.builder()
                    }
                }
                setBody {
                    add(returnStatement {
                        constructorCall {
                            className = requestName(interfName, method)
                            method.args.forEach {
                                addArg {
                                    it.apiType.modernSerialize(it.name)
                                }
                            }
                        }
                    })
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

            method.args.forEach { arg ->
                addField {
                    modifier = AccessModifier.PRIVATE
                    mutable = true
                    name = arg.name
                    value = variable(objectCreationType(arg.apiType))
                }
            }

            method.args.forEach { arg ->
                addMethod {
                    name = "get${camelToPascalCase(arg.name)}"
                    returnType = typeName(arg.type)
                    setBody {
                        add(returnStatement {
                            variable(arg.apiType.deserialize("this." + arg.name))
                        })
                    }
                }
            }

            addMethod {
                static = true
                name = "create"
                returnType = typeName(requestName(interfName, method))
                method.args.map { arg ->
                    addArg {
                        name = arg.name
                        type = typeName(arg.type)
                    }
                }
                setBody {
                    add(assignment {
                        left = variableDeclaration {
                            name = "instance"
                        }
                        right = constructorCall {
                            className = requestName(interfName, method)
                        }
                    })
                    method.args.forEach {
                        add(assignment {
                            left = expression("instance.${it.name}")
                            right = variable(it.apiType.serialize(it.name))
                        })
                    }
                    add(returnStatement {
                        variable("instance")
                    })
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

            addField {
                modifier = AccessModifier.PRIVATE
                mutable = true
                name = argName
                value = variable(objectCreationType(argApiType))
            }

            addMethod {
                name = "get${camelToPascalCase(argName)}"
                returnType = typeName(argType)
                legacyBody = {
                    legacyReturn {
                        legacyVariable(argApiType.deserialize("this.$argName"))
                    }
                }
            }
        }
    }

    private fun defaultResponseClass(interfName: String, method: MethodView): ClassBuilderOps {
        return {
            name = responseName(interfName, method)

            addField {
                modifier = AccessModifier.PUBLIC
                type = method.returnApiType.builder()
                name = "value"
                fromConstructor = true
            }
        }
    }

    private fun getClasses(): List<ClassBuilderOps> {
        val classes: MutableList<ClassBuilderOps> = mutableListOf()

        classes.add {
            name = "${c.module.getName().value}WebClientConfig"
            addField {
                name = "value"
                type = typeName("HttpClientConfig")
                fromConstructor = true
                getter = true
            }
        }

        exposedInterfaces(c).forEach { interf ->
            interf.methods.forEach { method ->
                if (method.hasArgs()) {
                    if (lang is TypeScript) {
                        classes.add(typeScriptRequestClass(interf.name, method))
                    } else {
                        classes.add(defaultRequestClass(interf.name, method))
                    }
                }
                if (method.returnApiType !is BaseApiType || method.returnApiType.name != BaseType.VOID) {
                    if (lang is TypeScript) {
                        classes.add(typeScriptResponseClass(interf.name, method))
                    } else {
                        classes.add(defaultResponseClass(interf.name, method))
                    }
                }
            }
        }

        return classes
    }

    override fun generateFileContent(): FileContent? {
        if (c.module.getWebSubmodule()?.getHttp() == null) {
            return null
        }

        val classes = getClasses()

        val moduleName = c.module.getName().value
        val view = CodeBuilder(c.language.base())
            .addOps {
                if (c.lang is TypeScript) {
                    namespace {
                        name = "$moduleName.Web"
                        addClass {
                            name = "${moduleName}WebClientConfig"

                            addField {
                                modifier = AccessModifier.PUBLIC
                                name = "value"
                                type = typeName("HttpClientConfig")
                                fromConstructor = true
                            }
                        }
                        classes.forEach(::addClass)
                    }
                }
                if (c.lang is Kotlin) {
                    classes.forEach(::legacyClassBlock)
                }
            }
            .build()
        return contentBuilder("webCommon.vm")
            .put("view", view)
            .build()
    }

    override fun supportsCodeBuilder(): Boolean {
        return lang is CSharp// || lang is Kotlin
    }

    override fun shouldGenerate(): Boolean {
        return exposedInterfaces(c).isNotEmpty()
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        getClasses().forEach {
            addClass(it)
        }
    }
}