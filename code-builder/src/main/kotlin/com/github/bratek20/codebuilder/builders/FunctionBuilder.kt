package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps
import com.github.bratek20.codebuilder.core.emptyBlock

class FunctionBuilder: CodeBlockBuilder {
    lateinit var name: String

    var override: Boolean = false
    var returnType: String? = null
    var args: List<Pair<String, String>> = emptyList()
    var body: CodeBlockBuilder = emptyBlock()

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
fun function(block: FunctionBuilderOps) = FunctionBuilder().apply(block)