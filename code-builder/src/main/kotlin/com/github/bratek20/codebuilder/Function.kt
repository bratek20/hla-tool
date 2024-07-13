package com.github.bratek20.codebuilder

class Function(
    private val override: Boolean = false,
    private val name: String,
    private val returnType: String? = null,
    private val args: List<Pair<String, String>>,
    private val body: CodeBlockBuilder
): CodeBlockBuilder {
    override fun apply(b: CodeBuilder) {
        val overridePart = if (override) "override " else ""
        val returnTypePart = if (returnType != null) ": $returnType" else ""

        b.line("${overridePart}fun $name(${args.joinToString { "${it.first}: ${it.second}" }})$returnTypePart {")
        b.tab()
        body.apply(b)
        b.untab()
        b.line("}")
    }
}
