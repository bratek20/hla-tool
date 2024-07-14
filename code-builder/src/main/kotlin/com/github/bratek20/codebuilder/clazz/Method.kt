package com.github.bratek20.codebuilder.clazz

import com.github.bratek20.codebuilder.BaseCodeBlockBuilder
import com.github.bratek20.codebuilder.CodeBlockBuilder
import com.github.bratek20.codebuilder.CodeBuilder
import com.github.bratek20.codebuilder.EmptyBlock

class Method: BaseCodeBlockBuilder() {
    var name: String = "someMethod"
    var override: Boolean = false
    var returnType: String? = null
    var args: List<Pair<String, String>> = emptyList()
    var body: CodeBlockBuilder = EmptyBlock()

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
fun method(block: Method.() -> Unit): Method {
    return Method().apply(block)
}
