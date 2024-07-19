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
        lineStart()
        accessor?.let {
            linePart("${it.name.lowercase()} ")
        }
        linePart("${c.lang.immutableFieldDeclaration()}$name: ")
        add(type)
        value?.let {
            linePart(" = ")
            add(it)
        }
        lineEnd("")
    }
}
typealias FieldBuilderOps = FieldBuilder.() -> Unit
fun field(block: FieldBuilderOps) = FieldBuilder().apply(block)

class ClassBuilder: CodeBlockBuilder {
    lateinit var name: String

    var implementedInterfaceName: String? = null

    private val body: MutableList<CodeBlockBuilder> = mutableListOf()

    fun method(block: MethodBuilderOps) {
        body.add(MethodBuilder().apply(block))
    }

    fun comment(value: String) {
        body.add(lineBlock("// $value"))
    }

    fun field(block: FieldBuilderOps) {
        body.add(FieldBuilder().apply(block))
    }

    fun constructorField(block: FieldBuilderOps) {

    }

    fun emptyLine() {
        body.add(emptyLineBlock())
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val implementsPart = implementedInterfaceName?.let { c.lang.implements() + it } ?: ""

        line("class $name${implementsPart} {")
        tab()
        body.forEach { add(it) }
        untab()
        line("}")
    }
}
typealias ClassBuilderOps = ClassBuilder.() -> Unit
fun CodeBuilder.classBlock(block: ClassBuilderOps) = add(ClassBuilder().apply(block))