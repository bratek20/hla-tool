package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.utils.camelToPascalCase

class FieldBuilder(
    private val endStatement: Boolean = true
): CodeBlockBuilder {
    lateinit var name: String

    var type: TypeBuilder? = null
    var legacyValue: CodeBuilderOps? = null
    var value: ExpressionBuilder? = null

    var modifier: AccessModifier = AccessModifier.PRIVATE
    var mutable = false
    var static = false

    var getter = false
    var fromConstructor = false

    override fun validate(c: CodeBuilderContext): ValidationResult {
        if (type == null) {
            if (!c.lang.supportsFieldTypeDeductionFromAssignedValue()) {
                return ValidationResult.failure("Field `$name` - type deduction not supported in ${c.lang.name()}")
            }
            else if (value == null) {
                return ValidationResult.failure("Field `$name` - type can not be deducted, value is required")
            }
        }

        return ValidationResult.success()
    }

    private fun getOperationsForCSharpGetter(): CodeBuilderOps = {
        lineStart("public ")
        add(type!!)
        lineEnd(" ${camelToPascalCase(name)} { get; }")
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps {
        if (getter && c.lang is CSharp) {
            return getOperationsForCSharpGetter()
        }
        return {
            lineStart()

            val finalModifier = if (getter) {
                AccessModifier.PUBLIC
            }
            else {
                modifier
            }

            if (finalModifier != c.lang.defaultAccessModifierForClassMembers()) {
                linePart("${finalModifier.name.lowercase()} ")
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

            when(c.lang.typeDeclarationStyle()) {
                TypeDeclarationStyle.TYPE_FIRST -> {
                    type?.let {
                        add(it)
                        linePart(" ")
                    }
                    linePart(name)
                }
                TypeDeclarationStyle.VARIABLE_FIRST -> {
                    linePart(name)
                    type?.let {
                        linePart(": ")
                        add(it)
                    }
                }
            }
            legacyValue?.let {
                linePart(" = ")
                addOps(it)
            }
            value?.let {
                linePart(" = ")
                add(it)
            }

            if(endStatement) {
                statementLineEnd()
            }
        }
    }
}
typealias FieldBuilderOps = FieldBuilder.() -> Unit
fun CodeBuilder.legacyField(block: FieldBuilderOps) = add(FieldBuilder().apply(block))
fun field(block: FieldBuilderOps) = FieldBuilder().apply(block)

class StaticMethodBuilder: MethodBuilder() {
    override fun beforeName(c: CodeBuilderContext): String {
        return "static " + super.beforeName(c)
    }
}
class ClassConstructorBuilder {
    private val args: MutableList<ArgumentBuilderOps> = mutableListOf()
    fun addArg(block: ArgumentBuilderOps) {
        args.add(block)
    }

    private var body: BodyBuilderOps? = null
    fun setBody(block: BodyBuilderOps) {
        body = block
    }

    fun getBody(): BodyBuilder? = body?.let { BodyBuilder().apply(it) }
    fun getArgs(): List<ArgumentBuilder> = args.map { ArgumentBuilder().apply(it) }
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
    private val fieldOps: MutableList<FieldBuilderOps> = mutableListOf()
    private val fields: MutableList<FieldBuilder> = mutableListOf()

    var legacyBody: CodeBuilderOps? = null
    private val staticMethods: MutableList<MethodBuilderOps> = mutableListOf()

    private var extends: ExtendsBuilderOps? = null
    fun extends(block: ExtendsBuilderOps) {
        extends = block
    }

    fun constructor(block: ClassConstructorBuilderOps) {
        constructor = ClassConstructorBuilder().apply(block)
    }

    //TODO-REF use addMethod and filter by static
    fun addStaticMethod(block: MethodBuilderOps) {
        staticMethods.add(block)
    }

    private val methods: MutableList<MethodBuilder> = mutableListOf()
    fun addMethod(ops: MethodBuilderOps) {
        methods.add(method(ops))
    }

    fun addField(ops: FieldBuilderOps) {
        fieldOps.add(ops)
        fields.add(field(ops))
    }

    private val passingArgs: MutableList<String> = mutableListOf()
    fun addPassingArg(argName: String) {
        passingArgs.add(argName)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        addOps(classDeclarationWithConstructor(c))
        tab()
        fieldOps.forEach { ops ->
            if (!c.lang.supportsFieldDeclarationInConstructor() || !field(ops).fromConstructor) {
                add(field(ops))
            }
        }
        legacyBody?.let { addOps(it) }
        methods.forEach { method ->
            add(method)
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

        if (c.lang is Kotlin && shouldGenerateConstructor()) {
            line("$beginningWithoutExtendOrImplements(")
            tab()
            addOps(getFieldsAndArgsOps())
            untab()
            val passingArgsPart = if (passingArgs.isNotEmpty()) {
                "(${passingArgs.joinToString(", ")})"
            }
            else {
                ""
            }
            line(")$extendsOrImplementsPart$passingArgsPart {")
            if (constructor?.getBody() != null) {
                tab()
                line("init {")
                tab()
                add(constructor!!.getBody()!!)
                untab()
                line("}")
                untab()
            }
        }
        else if (c.lang is TypeScript && shouldGenerateConstructor()) {
            line("$beginning {")
            tab()
            line("constructor(")
            tab()
            addOps(getFieldsAndArgsOps())
            untab()
            if (constructor?.getBody() != null) {
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
        else if (c.lang is CSharp && shouldGenerateConstructor()) {
            line("$beginning {")
            tab()
            line("public $name(")
            tab()
            addOps(getConstructorArgsOps())
            untab()

            if (passingArgs.isNotEmpty()) {
                line("): base(${passingArgs.joinToString(", ")}) {")
                line("}")
            }
            else if (constructorFields.isNotEmpty()) {
                line(") {")
                tab()
                constructorFields.forEach { field ->
                    if (field.getter) {
                        line("${camelToPascalCase(field.name)} = ${field.name};")
                    }
                    else {
                        line("this.${field.name} = ${field.name};")
                    }
                }
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

    private val constructorFields
        get() = fields.filter { it.fromConstructor }

    private fun getConstructorArgsOps(): CodeBuilderOps = {
        val constructorFields = fields.filter { it.fromConstructor }
        val finalArgs = constructorFields.map { field ->
            argument {
                name = field.name
                type = field.type!!
            }
        } + (constructor?.getArgs() ?: emptyList())

        finalArgs.forEachIndexed { idx, arg ->
            add(arg)
            if (idx != finalArgs.size - 1) {
                linePart(", ")
            }
            lineEnd()
        }
    }

    private fun getFieldsAndArgsOps(): CodeBuilderOps = {
        fieldOps.forEachIndexed { idx, field ->
            add(FieldBuilder(false).apply(field))
            if (idx != fieldOps.size - 1 || constructorArgs.isNotEmpty()) {
                linePart(",")
            }
            lineEnd()
        }
        constructorArgs.forEachIndexed { idx, arg ->
            add(arg)
            if (idx != constructorArgs.size - 1) {
                linePart(",")
            }
            lineEnd()
        }
    }

    private val constructorArgs
        get() = constructor?.getArgs() ?: emptyList()

    private fun shouldGenerateConstructor(): Boolean {
        if (constructor != null) {
            return true
        }
        return fields.any { it.fromConstructor }
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
fun CodeBuilder.legacyClassBlock(block: ClassBuilderOps) = add(ClassBuilder().apply(block))
fun classBlock(block: ClassBuilderOps) = ClassBuilder().apply(block)