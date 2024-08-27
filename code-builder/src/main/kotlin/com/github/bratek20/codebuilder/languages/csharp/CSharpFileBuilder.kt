package com.github.bratek20.codebuilder.languages.csharp

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class CSharpFileBuilder: TopLevelCodeBuilder() {
    private var namespaceName: String? = null

    private val usings = mutableListOf<String>()
    fun addUsing(using: String) {
        usings.add(using)
    }

    fun namespace(name: String) {
        namespaceName = name
    }

    override fun beforeOperations(): CodeBuilderOps = {
        if (usings.isNotEmpty()) {
            usings.forEach { line("using $it;") }
            emptyLine()
        }

        namespaceName?.let {
            line("namespace $it {")
            tab()
        }
    }

    override fun afterOperations(): CodeBuilderOps = {
        namespaceName?.let {
            untab()
            line("}")
        }
    }
}

typealias CSharpFileBuilderOps = CSharpFileBuilder.() -> Unit
fun CodeBuilder.cSharpFile(block: CSharpFileBuilderOps) = add(CSharpFileBuilder().apply(block))