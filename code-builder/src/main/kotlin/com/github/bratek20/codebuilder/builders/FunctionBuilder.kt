package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*

class FunctionBuilder: CodeBlockBuilder {
    var name: String = "someFunction"
    var override: Boolean = false
    var returnType: String? = null
    var args: List<Pair<String, String>> = emptyList()
    var body: CodeBlockBuilder = EmptyBlock()

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val returnTypePart = if (returnType != null) ": $returnType" else ""

        line("${c.lang.functionDeclarationKeyword()}$name(${args.joinToString { "${it.first}: ${it.second}" }})$returnTypePart {")
        tab()
        body.getOperations(c)
        untab()
        line("}")
    }
}
typealias FunctionBuilderOps = FunctionBuilder.() -> Unit