package com.github.bratek20.codebuilder.clazz

import com.github.bratek20.codebuilder.BaseCodeBlockBuilder
import com.github.bratek20.codebuilder.CodeBlockBuilder
import com.github.bratek20.codebuilder.CodeBuilder

class Method(
    private val override: Boolean = false,
    private val name: String,
    private val returnType: String? = null,
    private val args: List<Pair<String, String>>,
    private val body: CodeBlockBuilder
): BaseCodeBlockBuilder() {
    override fun apply(b: CodeBuilder) {
        val overridePart = if (override) "override " else ""
        val returnTypePart = if (returnType != null) ": $returnType" else ""

        b.line("${overridePart}${lang.methodDeclarationKeyword()}$name(${args.joinToString { "${it.first}: ${it.second}" }})$returnTypePart {")
        b.tab()
        body.apply(b)
        b.untab()
        b.line("}")
    }
}
