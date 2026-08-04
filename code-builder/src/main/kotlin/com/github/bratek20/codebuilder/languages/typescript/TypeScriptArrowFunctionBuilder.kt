package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.builders.BodyBuilder
import com.github.bratek20.codebuilder.builders.BodyBuilderOps
import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class TypeScriptArrowFunctionBuilder: CodeBlockBuilder {
    lateinit var argName: String

    private var body: BodyBuilderOps? = null
    fun setBody(block: BodyBuilderOps) {
        body = block
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart("$argName ${c.lang.lambdaArrow()} {")
        tab()
        body?.let { add(BodyBuilder().apply(it)) }
        untab()
        lineSoftStart("}")
    }
}
typealias TypeScriptArrowFunctionBuilderOps = TypeScriptArrowFunctionBuilder.() -> Unit
fun typeScriptArrowFunction(block: TypeScriptArrowFunctionBuilderOps) = TypeScriptArrowFunctionBuilder().apply(block)
