package com.github.bratek20.codebuilder.languages.typescript

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderException
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class TypeScriptPropertyBuilder: ExpressionBuilder {
    var key: String? = null
    var value: ExpressionBuilder? = null
    var blockValue: CodeBlockBuilder? = null

    override fun build(c: CodeBuilderContext): String {
        if (blockValue != null) {
            throw CodeBuilderException("Property `${key!!}` with block value is supported only by multiline structure")
        }
        return "${key!!}: ${value!!.build(c)}"
    }
}
typealias TypeScriptPropertyBuilderOps = TypeScriptPropertyBuilder.() -> Unit

abstract class TypeScriptPropertiesBuilder {
    protected val properties = mutableListOf<TypeScriptPropertyBuilder>()

    fun addProperty(block: TypeScriptPropertyBuilderOps) {
        properties.add(TypeScriptPropertyBuilder().apply(block))
    }
}

class TypeScriptStructureBuilder: TypeScriptPropertiesBuilder(), ExpressionBuilder {
    override fun build(c: CodeBuilderContext): String {
        if (properties.isEmpty()) {
            return "{}"
        }

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

class TypeScriptMultilineStructureBuilder: TypeScriptPropertiesBuilder(), CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        lineSoftStart("{")
        tab()
        properties.forEachIndexed { index, property ->
            lineSoftStart("${property.key!!}: ")
            property.value?.let { add(it) }
            property.blockValue?.let { add(it) }

            if (index != properties.size - 1) {
                lineSoftEnd(",")
            } else {
                lineEnd()
            }
        }
        untab()
        lineSoftStart("}")
    }
}
typealias TypeScriptMultilineStructureBuilderOps = TypeScriptMultilineStructureBuilder.() -> Unit
fun typeScriptMultilineStructure(block: TypeScriptMultilineStructureBuilderOps) =
    TypeScriptMultilineStructureBuilder().apply(block)
