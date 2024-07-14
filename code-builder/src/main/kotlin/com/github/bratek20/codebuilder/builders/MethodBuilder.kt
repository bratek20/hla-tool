package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*

class MethodBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    var name: String = "someMethod"
    var override: Boolean = false
    var returnType: String? = null
    var args: List<Pair<String, String>> = emptyList()
    var body: CodeBlockBuilder = EmptyBlock()

    override fun applyOperations(b: CodeBuilder) {
        val overridePart = if (override) "override " else ""
        val returnTypePart = if (returnType != null) ": $returnType" else ""

        b.line("${overridePart}${lang.methodDeclarationKeyword()}$name(${args.joinToString { "${it.first}: ${it.second}" }})$returnTypePart {")
        b.tab()
        body.applyOperations(b)
        b.untab()
        b.line("}")
    }
}
typealias MethodBuilderOps = MethodBuilder.() -> Unit
