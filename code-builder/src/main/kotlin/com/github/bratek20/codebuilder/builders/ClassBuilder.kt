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
fun field(block: FieldBuilderOps) = FieldBuilder().apply(block)

class ClassBuilder: CodeBlockBuilder {
    lateinit var name: String

    var implementedInterfaceName: String? = null
    var body: CodeBuilderOps? = null

    private val constructorFields: MutableList<FieldBuilder> = mutableListOf()

    fun constructorField(block: FieldBuilderOps) {
        constructorFields.add(FieldBuilder().apply(block))
    }

    fun staticMethod(block: MethodBuilderOps) {
        body = {
            line("companion object {")
            tab()
            add(MethodBuilder().apply(block))
            untab()
            line("}")
        }
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        add(classDeclaration(c))
        tab()
        body?.let { add(it) }
        untab()
        line("}")
    }

    private fun classDeclaration(c: CodeBuilderContext): CodeBuilderOps = {
        val implementsPart = implementedInterfaceName?.let { c.lang.implements() + it } ?: ""

        if (c.lang is Kotlin && constructorFields.isNotEmpty()) {
            line("class $name${implementsPart}(")
            tab()
            constructorFields.forEach { field ->
                add(field)
                linePart(",")
            }
            untab()
            line(") {")
        }
        else {
            line("class $name${implementsPart} {")
        }
    }
}
typealias ClassBuilderOps = ClassBuilder.() -> Unit
fun CodeBuilder.classBlock(block: ClassBuilderOps) = add(ClassBuilder().apply(block))