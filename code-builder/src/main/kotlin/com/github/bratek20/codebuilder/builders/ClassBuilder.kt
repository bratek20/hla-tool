package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder

enum class FieldAccessor {
    PRIVATE, PROTECTED, PUBLIC
}

class FieldBuilder: CodeBlockBuilder {
    lateinit var name: String

    var type: TypeBuilder? = null

    var legacyValue: CodeBuilderOps? = null
    var value: ExpressionBuilder? = null

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
        legacyValue?.let {
            linePart(" = ")
            addOps(it)
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
        args.forEachIndexed { idx, argOps ->
            argument(argOps)
            if (idx != args.size - 1) {
                linePart(",")
            }
            lineEnd()
        }
    }

    private var body: BodyBuilderOps? = null
    fun setBody(block: BodyBuilderOps) {
        body = block
    }

    fun getBody(): BodyBuilder? = body?.let { BodyBuilder().apply(it) }
}
typealias ClassConstructorBuilderOps = ClassConstructorBuilder.() -> Unit

class ExtendsBuilder: LinePartBuilder {
    lateinit var className: String
    var generic: TypeBuilder? = null

    override fun build(c: CodeBuilderContext): String {
        val genericPart = generic?.let { "<${it.build(c)}>" } ?: ""
        return "$className$genericPart"
    }
}
typealias ExtendsBuilderOps = ExtendsBuilder.() -> Unit

open class ClassBuilder: CodeBlockBuilder {
    open fun beforeClassKeyword(): String = ""

    lateinit var name: String

    var implements: String? = null

    private var constructor: ClassConstructorBuilder? = null
    private val fields: MutableList<FieldBuilderOps> = mutableListOf()
    var legacyBody: CodeBuilderOps? = null

    private val staticMethods: MutableList<MethodBuilderOps> = mutableListOf()

    private var extends: ExtendsBuilderOps? = null
    fun extends(block: ExtendsBuilderOps) {
        extends = block
    }

    fun constructor(block: ClassConstructorBuilderOps) {
        constructor = ClassConstructorBuilder().apply(block)
    }

    fun addStaticMethod(block: MethodBuilderOps) {
        staticMethods.add(block)
    }

    private val methods: MutableList<MethodBuilderOps> = mutableListOf()
    fun addMethod(block: MethodBuilderOps) {
        methods.add(block)
    }

    fun addField(block: FieldBuilderOps) {
        fields.add(block)
    }

    private val passingArgs: MutableList<String> = mutableListOf()
    fun addPassingArg(argName: String) {
        passingArgs.add(argName)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        addOps(classDeclarationWithConstructor(c))
        tab()
        fields.forEach { fieldOps ->
            field(fieldOps)
        }
        legacyBody?.let { addOps(it) }
        methods.forEach { methodOps ->
            legacyMethod(methodOps)
        }
        if (staticMethods.isNotEmpty()) {
            addOps(staticMethodsSection(c))
        }
        untab()
        line("}")
    }

    private fun classDeclarationWithConstructor(c: CodeBuilderContext): CodeBuilderOps = {
        val classPart = beforeClassKeyword() + c.lang.defaultTopLevelAccessor() + "class "
        var extendsOrImplementsPart = implements?.let { c.lang.implements() + it } ?: ""
        extendsOrImplementsPart = extends?.let { c.lang.extends() + ExtendsBuilder().apply(it).build(c) } ?: extendsOrImplementsPart
        val beginningWithoutExtendOrImplements = "$classPart$name"
        val beginning = "$beginningWithoutExtendOrImplements$extendsOrImplementsPart"

        if (c.lang is Kotlin && constructor != null) {
            line("$beginningWithoutExtendOrImplements(")
            tab()
            addOps(constructor!!.getFieldsAndArgsOps())
            untab()
            val passingArgsPart = if (passingArgs.isNotEmpty()) {
                "(${passingArgs.joinToString(", ")})"
            }
            else {
                ""
            }
            line(")$extendsOrImplementsPart$passingArgsPart {")
            if (constructor!!.getBody() != null) {
                tab()
                line("init {")
                tab()
                add(constructor!!.getBody()!!)
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
            addOps(constructor!!.getFieldsAndArgsOps())
            untab()
            if (constructor!!.getBody() != null) {
                line(") {")
                tab()
                add(constructor!!.getBody()!!)
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
        else if (c.lang is CSharp && constructor != null) {
            line("$beginning {")
            tab()
            line("public $name(")
            tab()
            addOps(constructor!!.getFieldsAndArgsOps())
            untab()
            if (passingArgs.isNotEmpty()) {
                line("): base(${passingArgs.joinToString(", ")}) {")
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
                legacyMethod(methodOps)
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