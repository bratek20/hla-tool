package com.github.bratek20.codebuilder.languages.csharp

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class CSharpFileBuilder: TopLevelCodeBuilder() {
    private var namespace: CSharpNamespaceBuilderOps? = null

    private val usings = mutableListOf<String>()
    fun addUsing(using: String) {
        usings.add(using)
    }

    fun namespace(ops: CSharpNamespaceBuilderOps) {
        namespace = ops
    }

    override fun beforeOperations(): CodeBuilderOps = {
        if (usings.isNotEmpty()) {
            usings.forEach { line("using $it;") }
            emptyLine()
        }

        namespace?.let { add(CSharpNamespaceBuilder.create(it)) }
    }
}

typealias CSharpFileBuilderOps = CSharpFileBuilder.() -> Unit
fun CodeBuilder.cSharpFile(block: CSharpFileBuilderOps) = add(CSharpFileBuilder().apply(block))