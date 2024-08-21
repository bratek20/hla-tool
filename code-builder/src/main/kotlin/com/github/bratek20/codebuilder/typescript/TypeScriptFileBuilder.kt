package com.github.bratek20.codebuilder.typescript

import com.github.bratek20.codebuilder.builders.FileBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class TypeScriptFileBuilder: FileBuilder() {
    private var namespace: NamespaceBuilderOps? = null

    fun namespace(block: NamespaceBuilderOps) {
        namespace = block
    }

    override fun beforeOperations(): CodeBuilderOps = {
        namespace?.let { namespace(it) }
    }
}

typealias TypeScriptFileBuilderOps = TypeScriptFileBuilder.() -> Unit
fun CodeBuilder.typeScriptFile(block: TypeScriptFileBuilderOps) = add(TypeScriptFileBuilder().apply(block))