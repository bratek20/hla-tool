package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps


class TypeScriptStructureBuilder: CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        line("{")
        line("}")
    }
}
typealias TypeScriptStructureBuilderOps = TypeScriptStructureBuilder.() -> Unit
fun typeScriptStructure(block: TypeScriptStructureBuilderOps) = TypeScriptStructureBuilder().apply(block)