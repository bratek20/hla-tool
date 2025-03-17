package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.codebuilder.types.TypeBuilderProvider
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
    var setter = false
    var fromConstructor = false

    var defaultValue: ExpressionBuilder? = null

    private val annotations: MutableList<String> = mutableListOf()
    fun addAnnotation(name: String) {
        annotations.add(name)
    }

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

    private fun getOperationsForCSharpGetterAndSetter(): CodeBuilderOps = {
        lineStart("public ")
        add(type!!)
        val getPart = if (getter) "get; " else ""
        val setPart = if (setter) "set; " else ""
        linePart(" ${camelToPascalCase(name)} { $getPart$setPart}")

        defaultValue?.let {
            linePart(" = ")
            add(it)
            statementLineEnd()
        } ?: lineEnd()
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps {
        if ((getter || setter) && c.lang is CSharp) {
            return getOperationsForCSharpGetterAndSetter()
        }
        return {
            if (annotations.isNotEmpty()) {
                line("[${annotations[0]}]")
            }
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
    lateinit var name: String
    private var generics: MutableList<TypeBuilder> = mutableListOf()

    fun addGeneric(block: TypeBuilderProvider) {
        generics.add(block())
    }

    override fun build(c: CodeBuilderContext): String {
        val genericPart = if (generics.isNotEmpty()) {
            "<${generics.joinToString(", ") { it.build(c) }}>"
        }
        else {
            ""
        }
        return "$name$genericPart"
    }
}
typealias ExtendsBuilderOps = ExtendsBuilder.() -> Unit

open class ClassBuilder: CodeBlockBuilder {
    open fun beforeClassKeyword(): String = ""

    lateinit var name: String

    var partial = false

    var implements: String? = null

    var dataClass: Boolean = false

    private var constructor: ClassConstructorBuilder? = null
    private val fieldOps: MutableList<FieldBuilderOps> = mutableListOf()
    private val fields: MutableList<FieldBuilder> = mutableListOf()

    var legacyBody: CodeBuilderOps? = null


    private var extends: ExtendsBuilderOps? = null
    fun extends(block: ExtendsBuilderOps) {
        extends = block
    }

    fun setConstructor(block: ClassConstructorBuilderOps) {
        constructor = ClassConstructorBuilder().apply(block)
    }

    private val allMethods: MutableList<MethodBuilder> = mutableListOf()
    private val staticMethods
        get() = allMethods.filter { it.static }
    private val methods
        get() = allMethods.filter { !it.static }

    private val innerClasses: MutableList<ClassBuilder> = mutableListOf()

    fun addMethod(ops: MethodBuilderOps) {
        allMethods.add(method(ops))
    }

    fun addField(ops: FieldBuilderOps) {
        fieldOps.add(ops)
        fields.add(field(ops))
    }

    fun addClass(block: ClassBuilderOps) {
        innerClasses.add(ClassBuilder().apply(block))
    }

    private val passingArgs2: MutableList<ExpressionBuilder> = mutableListOf()
    fun addPassingArg(arg: ExpressionBuilderProvider) {
        passingArgs2.add(arg())
    }

    private fun hasPassingArgs() = passingArgs2.isNotEmpty()
    private fun allPassingArgs(c: CodeBuilderContext) =
        passingArgs2.joinToString(", ") { it.build(c) }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        addOps(classDeclarationWithFieldConstructor(c))
        tab()
        fieldOps.forEach { ops ->
            if (!c.lang.supportsFieldDeclarationInConstructor() || !field(ops).fromConstructor) {
                add(field(ops))
            }
        }
        addOps(nonFieldConstructorOps(c))
        legacyBody?.let { addOps(it) }
        methods.forEach { method ->
            add(method)
        }
        innerClasses.forEach { c ->
            add(c)
        }
        if (staticMethods.isNotEmpty()) {
            addOps(staticMethodsSection(c))
        }
        untab()
        line("}")
    }

    private fun nonFieldConstructorOps(c: CodeBuilderContext): CodeBuilderOps = {
        if (c.lang is CSharp && shouldGenerateConstructor()) {
            if (fields.isNotEmpty()) {
                emptyLine()
            }

            line("public $name(")
            tab()
            addOps(getConstructorArgsOps())
            untab()

            if (hasPassingArgs()) {
                line("): base(${allPassingArgs(c)}) {")
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
                constructor?.getBody()?.let {
                    add(it)
                }
                untab()
                line("}")
            }
            else if (constructor?.getBody() != null) {
                line(") {")
                tab()
                add(constructor!!.getBody()!!)
                untab()
                line("}")
            }
            else {
                line(") {}")
            }
        }
    }

    private fun classDeclarationWithFieldConstructor(c: CodeBuilderContext): CodeBuilderOps = {
        val finalClassKeyword = if(dataClass && c.lang is Kotlin) {
            "data class "
        }
        else {
            "class "
        }

        val partialPart = if (partial) "partial " else ""
        val classPart = beforeClassKeyword() + c.lang.defaultTopLevelAccessor() + partialPart + finalClassKeyword
        var extendsOrImplementsPart = implements?.let { c.lang.implements() + it } ?: ""
        extendsOrImplementsPart = extends?.let { c.lang.extends() + ExtendsBuilder().apply(it).build(c) } ?: extendsOrImplementsPart
        val beginningWithoutExtendOrImplements = "$classPart$name"
        val beginning = "$beginningWithoutExtendOrImplements$extendsOrImplementsPart"

        if (c.lang is Kotlin && shouldGenerateConstructor()) {
            line("$beginningWithoutExtendOrImplements(")
            tab()
            addOps(getFieldsAndArgsOps())
            untab()
            val passingArgsPart = if (hasPassingArgs()) {
                "(${allPassingArgs(c)})"
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
            else if (hasPassingArgs()) {
                line(") {")
                tab()
                line("super(${allPassingArgs(c)})")
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
            lineSoftStart()
            add(arg)
            if (idx != finalArgs.size - 1) {
                linePart(",")
            }
            lineEnd()
        }
    }

    private fun getFieldsAndArgsOps(): CodeBuilderOps = {
        fieldOps.forEachIndexed { idx, field ->
            val builder = FieldBuilder(false).apply(field)
            if (builder.fromConstructor) {
                add(builder)
                if (idx != fieldOps.size - 1 || constructorArgs.isNotEmpty()) {
                    linePart(",")
                }
                lineEnd()
            }
        }
        constructorArgs.forEachIndexed { idx, arg ->
            lineSoftStart()
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
            staticMethods.forEach { method ->
                add(method)
            }
            untab()
            line("}")
        }
        else {
            staticMethods.forEach { method ->
                add(method)
            }
        }
    }
}
typealias ClassBuilderOps = ClassBuilder.() -> Unit
fun CodeBuilder.legacyClassBlock(block: ClassBuilderOps) = add(ClassBuilder().apply(block))
fun classBlock(block: ClassBuilderOps) = ClassBuilder().apply(block)