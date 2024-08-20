package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class EnumBuilder: CodeBlockBuilder {
    lateinit var name: String

    private val values: MutableList<String> = mutableListOf()

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps {
        return {
            line("enum class $name {")
            tab()
            values.forEach {
                line("$it,")
            }
            untab()
            line("}")
        }
    }

    fun addValue(value: String) {
        values.add(value)
    }
}
typealias EnumBuilderOps = EnumBuilder.() -> Unit
fun CodeBuilder.enum(block: EnumBuilderOps) = add(EnumBuilder().apply(block))