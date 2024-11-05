package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext

class TypeScriptPropertyBuilder: ExpressionBuilder {
    var key: String? = null
    var value: ExpressionBuilder? = null

    override fun build(c: CodeBuilderContext): String {
        return "${key!!}: ${value!!.build(c)}"
    }
}

typealias TypeScriptPropertyBuilderOps = TypeScriptPropertyBuilder.() -> Unit
class TypeScriptStructureBuilder: ExpressionBuilder {
    private val properties = mutableListOf<TypeScriptPropertyBuilder>()

    fun addProperty(block: TypeScriptPropertyBuilderOps) {
        properties.add(TypeScriptPropertyBuilder().apply(block))
    }

    override fun build(c: CodeBuilderContext): String {
        val b = StringBuilder()
        b.append("{ ")
        properties.forEachIndexed { index, prop ->
            b.append(prop.build(c))
            if (index != properties.size - 1) {
                b.append(", ")
            }
        }
        b.append(" }")
        return b.toString()
    }
}
typealias TypeScriptStructureBuilderOps = TypeScriptStructureBuilder.() -> Unit
fun typeScriptStructure(block: TypeScriptStructureBuilderOps) = TypeScriptStructureBuilder().apply(block)