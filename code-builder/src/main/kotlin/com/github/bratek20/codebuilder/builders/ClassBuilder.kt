package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*
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
    }
}
typealias FieldBuilderOps = FieldBuilder.() -> Unit
fun field(block: FieldBuilderOps) = FieldBuilder().apply(block)

class ClassBuilder: CodeBlockBuilder {
    var name: String = "SomeClass"
    var implementedInterfaceName: String? = null

    private val body: MutableList<CodeBlockBuilder> = mutableListOf()

    fun method(block: MethodBuilderOps) {
        body.add(MethodBuilder().apply(block))
    }

    fun comment(value: String) {
        body.add(OneLineBlock("// $value"))
    }

    fun field(block: FieldBuilderOps) {
        body.add(FieldBuilder().apply(block))
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val implementsPart = implementedInterfaceName?.let { c.lang.implements() + it } ?: ""

        line("class $name${implementsPart} {")
        tab()
        applyBodyOperations(this)
        untab()
        line("}")
    }

    fun applyBodyOperations(b: CodeBuilder) {
        body.forEach { b.add(it) }
    }
}
typealias ClassBuilderOps = ClassBuilder.() -> Unit