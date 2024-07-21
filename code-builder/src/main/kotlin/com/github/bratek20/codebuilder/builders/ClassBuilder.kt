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

class StaticMethodBuilder: MethodBuilder() {
    override fun beforeName(c: CodeBuilderContext): String {
        return "static " + super.beforeName(c)
    }
}
class ClassConstructorBuilder {
    val fields: MutableList<FieldBuilderOps> = mutableListOf()
    fun addField(block: FieldBuilderOps) {
        fields.add(block)
    }

    val args: MutableList<ArgumentBuilderOps> = mutableListOf()
    fun addArg(block: ArgumentBuilderOps) {
        args.add(block)
    }

    fun getFieldsAndArgsOps(): CodeBuilderOps = {
        fields.forEach { fieldOps ->
            field(fieldOps)
            linePart(",")
            lineEnd()
        }
        args.forEach { argOps ->
            argument(argOps)
            linePart(",")
            lineEnd()
        }
    }
    var body: CodeBuilderOps? = null
}
typealias ClassConstructorBuilderOps = ClassConstructorBuilder.() -> Unit

open class ClassBuilder: CodeBlockBuilder {
    open fun beforeClassKeyword(): String = ""

    lateinit var name: String

    var implementedInterfaceName: String? = null
    private var constructor: ClassConstructorBuilder? = null
    var body: CodeBuilderOps? = null

    private val staticMethods: MutableList<MethodBuilderOps> = mutableListOf()

    fun constructor(block: ClassConstructorBuilderOps) {
        constructor = ClassConstructorBuilder().apply(block)
    }

    fun staticMethod(block: MethodBuilderOps) {
        staticMethods.add(block)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        add(classDeclarationWithConstructor(c))
        tab()
        body?.let { add(it) }
        if (staticMethods.isNotEmpty()) {
            add(staticMethodsSection(c))
        }
        untab()
        line("}")
    }

    private fun classDeclarationWithConstructor(c: CodeBuilderContext): CodeBuilderOps = {
        val classPart = beforeClassKeyword() + "class "
        val implementsPart = implementedInterfaceName?.let { c.lang.implements() + it } ?: ""
        val beginning = "$classPart$name$implementsPart"

        if (c.lang is Kotlin && constructor != null) {
            line("$beginning(")
            tab()
            add(constructor!!.getFieldsAndArgsOps())
            untab()
            line(") {")
            if (constructor!!.body != null) {
                tab()
                line("init {")
                tab()
                add(constructor!!.body!!)
                untab()
                line("}")
                untab()
            }
        }
        else if (c.lang is TypeScript && constructor != null) {
            line("$beginning {")
            tab()
            line("constructor(")
            tab()
            add(constructor!!.getFieldsAndArgsOps())
            untab()
            if (constructor!!.body != null) {
                line(") {")
                tab()
                add(constructor!!.body!!)
                untab()
                line("}")
            }
            else {
                line(") {}")
            }
            untab()
        }
        else {
            line("$beginning {")
        }
    }

    private fun staticMethodsSection(c: CodeBuilderContext): CodeBuilderOps = {
        if (c.lang is Kotlin) {
            line("companion object {")
            tab()
            staticMethods.forEach { methodOps ->
                method(methodOps)
            }
            untab()
            line("}")
        }
        else if (c.lang is TypeScript) {
            staticMethods.forEach { methodOps ->
                add(StaticMethodBuilder().apply(methodOps))
            }
        }
    }
}
typealias ClassBuilderOps = ClassBuilder.() -> Unit
fun CodeBuilder.classBlock(block: ClassBuilderOps) = add(ClassBuilder().apply(block))