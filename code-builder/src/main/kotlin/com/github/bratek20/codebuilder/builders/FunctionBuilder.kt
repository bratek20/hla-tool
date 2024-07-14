package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*

class FunctionBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    var name: String = "someFunction"
    var override: Boolean = false
    var returnType: String? = null
    var args: List<Pair<String, String>> = emptyList()
    var body: CodeBlockBuilder = EmptyBlock()

    override fun applyOperations(b: CodeBuilder) {
        val returnTypePart = if (returnType != null) ": $returnType" else ""

        b.line("${lang.functionDeclarationKeyword()}$name(${args.joinToString { "${it.first}: ${it.second}" }})$returnTypePart {")
        b.tab()
        body.applyOperations(b)
        b.untab()
        b.line("}")
    }
}
typealias FunctionBuilderOps = FunctionBuilder.() -> Unit