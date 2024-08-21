package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder

enum class FieldAccessor {
    PRIVATE, PROTECTED, PUBLIC
}

class FieldBuilder: CodeBlockBuilder {
    lateinit var name: String

    var type: TypeBuilder? = null

    var value: CodeBuilderOps? = null
    var accessor: FieldAccessor? = null
    var mutable = false
    var static = false

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart()
        accessor?.let {
            linePart("${it.name.lowercase()} ")
        }
        if (static) {
            linePart("static ")
        }
        if (mutable) {
            linePart(c.lang.mutableFieldDeclaration())
        }
        else {
            linePart(c.lang.immutableFieldDeclaration())
        }
        linePart(name)
        type?.let {
            linePart(": ")
            add(it)
        }
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

    var implements: String? = null
    var extends: String? = null
    private var constructor: ClassConstructorBuilder? = null
    private val fields: MutableList<FieldBuilderOps> = mutableListOf()
    var body: CodeBuilderOps? = null

    private val staticMethods: MutableList<MethodBuilderOps> = mutableListOf()

    fun constructor(block: ClassConstructorBuilderOps) {
        constructor = ClassConstructorBuilder().apply(block)
    }

    fun staticMethod(block: MethodBuilderOps) {
        staticMethods.add(block)
    }

    fun addField(block: FieldBuilderOps) {
        fields.add(block)
    }

    private val passingArgs: MutableList<String> = mutableListOf()
    fun addPassingArg(argName: String) {
        passingArgs.add(argName)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        add(classDeclarationWithConstructor(c))
        tab()
        fields.forEach { fieldOps ->
            field(fieldOps)
        }
        body?.let { add(it) }
        if (staticMethods.isNotEmpty()) {
            add(staticMethodsSection(c))
        }
        untab()
        line("}")
    }

    private fun classDeclarationWithConstructor(c: CodeBuilderContext): CodeBuilderOps = {
        val classPart = beforeClassKeyword() + "class "
        var extendsOrImplementsPart = implements?.let { c.lang.implements() + it } ?: ""
        extendsOrImplementsPart = extends?.let { c.lang.extends() + it } ?: extendsOrImplementsPart
        val beginningWithoutExtendOrImplements = "$classPart$name"
        val beginning = "$beginningWithoutExtendOrImplements$extendsOrImplementsPart"

        if (c.lang is Kotlin && constructor != null) {
            line("$beginningWithoutExtendOrImplements(")
            tab()
            add(constructor!!.getFieldsAndArgsOps())
            untab()
            val passingArgsPart = if (passingArgs.isNotEmpty()) {
                "(${passingArgs.joinToString(", ")})"
            }
            else {
                ""
            }
            line(")$extendsOrImplementsPart$passingArgsPart {")
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
            else if (passingArgs.isNotEmpty()) {
                line(") {")
                tab()
                line("super(${passingArgs.joinToString(", ")})")
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