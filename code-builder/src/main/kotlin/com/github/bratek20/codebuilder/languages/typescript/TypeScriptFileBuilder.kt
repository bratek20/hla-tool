package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class TypeScriptFileBuilder: TopLevelCodeBuilder() {
    private var namespace: TypeScriptNamespaceBuilderOps? = null

    fun namespace(block: TypeScriptNamespaceBuilderOps) {
        namespace = block
    }

    override fun beforeOperations(): CodeBuilderOps = {
        namespace?.let { add(typeScriptNamespace(it)) }
    }
}

typealias TypeScriptFileBuilderOps = TypeScriptFileBuilder.() -> Unit
fun CodeBuilder.typeScriptFile(block: TypeScriptFileBuilderOps) = add(TypeScriptFileBuilder().apply(block))