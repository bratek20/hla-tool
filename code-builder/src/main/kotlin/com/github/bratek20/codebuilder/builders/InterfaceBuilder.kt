package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.utils.camelToPascalCase

class InterfaceFieldBuilder: CodeBlockBuilder {
    lateinit var name: String
    lateinit var type: TypeBuilder

    var optional: Boolean = false

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        when (c.lang) {
            is CSharp -> {
                lineStart()
                add(type)
                if (optional) {
                    linePart("?")
                }
                lineEnd(" ${camelToPascalCase(name)} { get; set; }")
            }
            is TypeScript -> {
                lineStart(name)
                if (optional) {
                    linePart("?")
                }
                linePart(": ")
                add(type)
                lineEnd()
            }
            else -> {
                lineStart("val $name: ")
                add(type)
                if (optional) {
                    linePart("?")
                }
                lineEnd()
            }
        }
    }
}
typealias InterfaceFieldBuilderOps = InterfaceFieldBuilder.() -> Unit

open class InterfaceBuilder: CodeBlockBuilder {
    lateinit var name: String

    private val fields: MutableList<InterfaceFieldBuilderOps> = mutableListOf()
    fun addField(block: InterfaceFieldBuilderOps) {
        fields.add(block)
    }

    private val methods: MutableList<InterfaceMethodBuilderOps> = mutableListOf()
    fun addMethod(block: InterfaceMethodBuilderOps) {
        methods.add(block)
    }

    protected open fun beforeInterfaceKeyword(): String = ""

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val x = beforeInterfaceKeyword() + c.lang.defaultTopLevelAccessor() + "interface " + name
        line("$x {")

        tab()
        addMany(fields.map { fieldOps ->
            InterfaceFieldBuilder().apply(fieldOps)
        })
        if (fields.isNotEmpty() && methods.isNotEmpty()) {
            emptyLine()
        }
        addManyWithEmptyLineBetween(methods.map { methodOps ->
            InterfaceMethodBuilder.create(methodOps)
        })
        untab()

        line("}")
    }
}
typealias InterfaceBuilderOps = InterfaceBuilder.() -> Unit
fun CodeBuilder.interfaceBlock(block: InterfaceBuilderOps) = add(InterfaceBuilder().apply(block))
