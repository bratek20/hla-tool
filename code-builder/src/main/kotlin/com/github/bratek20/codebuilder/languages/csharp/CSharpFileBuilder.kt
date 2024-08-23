package com.github.bratek20.codebuilder.languages.csharp

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class CSharpFileBuilder: TopLevelCodeBuilder() {
    private var namespace: CSharpNamespaceBuilderOps? = null

    fun namespace(ops: CSharpNamespaceBuilderOps) {
        namespace = ops
    }

    override fun beforeOperations(): CodeBuilderOps = {
        namespace?.let { add(CSharpNamespaceBuilder.create(it)) }
    }
}

typealias CSharpFileBuilderOps = CSharpFileBuilder.() -> Unit
fun CodeBuilder.cSharpFile(block: CSharpFileBuilderOps) = add(CSharpFileBuilder().apply(block))