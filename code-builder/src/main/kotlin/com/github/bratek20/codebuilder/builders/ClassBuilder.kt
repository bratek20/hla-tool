package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*
import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder

class FieldBuilder: CodeBlockBuilder {
    lateinit var name: String
    lateinit var type: TypeBuilder

    var value: String? = null

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        linePart("val $name: ")
        add(type)
        value?.let {
            linePart(" = $it")
        }
        endLinePart()
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
fun classBlock(block: ClassBuilderOps) = ClassBuilder().apply(block)