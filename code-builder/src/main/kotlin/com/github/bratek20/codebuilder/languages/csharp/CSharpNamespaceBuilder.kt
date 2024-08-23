package com.github.bratek20.codebuilder.languages.csharp

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.*

class CSharpNamespaceBuilder: CodeBlockBuilder {
    lateinit var name: String

    private val body: MutableList<CodeBlockBuilder> = mutableListOf()

    fun addEnum(block: EnumBuilderOps) {
        body.add(EnumBuilder().apply(block))
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        line("namespace $name {")
        tab()
        body.forEach { add(it) }
        untab()
        line("}")
    }

    companion object {
        fun create(block: CSharpNamespaceBuilderOps): CSharpNamespaceBuilder {
            return CSharpNamespaceBuilder().apply(block)
        }
    }
}
typealias CSharpNamespaceBuilderOps = CSharpNamespaceBuilder.() -> Unit