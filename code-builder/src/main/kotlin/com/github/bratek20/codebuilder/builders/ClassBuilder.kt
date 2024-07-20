package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder

enum class FieldAccessor {
    PRIVATE, PROTECTED, PUBLIC
}

class FieldBuilder: CodeBlockBuilder {
    lateinit var name: String
    lateinit var type: TypeBuilder

    var value: LinePartBuilder? = null
    var accessor: FieldAccessor? = null

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart()
        accessor?.let {
            linePart("${it.name.lowercase()} ")
        }
        linePart("${c.lang.immutableFieldDeclaration()}$name: ")
        add(type)
        value?.let {
            linePart(" = ")
            add(it)
        }
        lineSoftEnd()
    }
}
typealias FieldBuilderOps = FieldBuilder.() -> Unit
fun CodeBuilder.field(block: FieldBuilderOps) = add(FieldBuilder().apply(block))

open class ClassBuilder: CodeBlockBuilder {
    open fun beforeClassKeyword(): String = ""

    lateinit var name: String

    var implementedInterfaceName: String? = null
    var body: CodeBuilderOps? = null

    private val constructorFields: MutableList<FieldBuilderOps> = mutableListOf()
    private val staticMethods: MutableList<MethodBuilderOps> = mutableListOf()

    fun constructorField(block: FieldBuilderOps) {
        constructorFields.add(block)
    }

    fun staticMethod(block: MethodBuilderOps) {
        staticMethods.add(block)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        add(classDeclaration(c))
        tab()
        body?.let { add(it) }
        if (staticMethods.isNotEmpty()) {
            add(staticMethodsSection(c))
        }
        untab()
        line("}")
    }

    private fun classDeclaration(c: CodeBuilderContext): CodeBuilderOps = {
        val classPart = beforeClassKeyword() + "class "
        val implementsPart = implementedInterfaceName?.let { c.lang.implements() + it } ?: ""
        val beginning = "$classPart$name$implementsPart"

        if (c.lang is Kotlin && constructorFields.isNotEmpty()) {
            line("$beginning(")
            tab()
            constructorFields.forEach { fieldOps ->
                field(fieldOps)
                linePart(",")
                lineEnd()
            }
            untab()
            line(") {")
        }
        else {
            line("$beginning {")
        }
    }

    private fun staticMethodsSection(c: CodeBuilderContext): CodeBuilderOps = {
        line("companion object {")
        tab()
        staticMethods.forEach { methodOps ->
            method(methodOps)
        }
        untab()
        line("}")
    }
}
typealias ClassBuilderOps = ClassBuilder.() -> Unit
fun CodeBuilder.classBlock(block: ClassBuilderOps) = add(ClassBuilder().apply(block))