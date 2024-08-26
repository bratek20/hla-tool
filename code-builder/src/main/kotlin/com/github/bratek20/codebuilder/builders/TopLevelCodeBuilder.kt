package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

open class TopLevelCodeBuilder: CodeBlockBuilder {
    private val ops: MutableList<CodeBuilderOps> = mutableListOf()

    protected open fun beforeOperations(): CodeBuilderOps = {}
    protected open fun afterOperations(): CodeBuilderOps = {}

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        add(beforeOperations())

        ops.forEachIndexed { idx, op ->
            add(op)
            if (idx != ops.size - 1) {
                emptyLine()
            }
        }

        add(afterOperations())
    }

    fun addInterface(ops: InterfaceBuilderOps) {
        this.ops.add {
            interfaceBlock(ops)
        }
    }

    fun addClass(classBlock: ClassBuilderOps) {
        ops.add {
            classBlock(classBlock)
        }
    }

    fun addFunction(function: FunctionBuilderOps) {
        ops.add {
            function(function)
        }
    }

    fun addEnum(enumBlock: EnumBuilderOps) {
        ops.add {
            enum(enumBlock)
        }
    }

    fun addFunctionCall(functionCall: FunctionCallBuilderOps) {
        ops.add {
            functionCall(functionCall)
        }
    }
}
typealias FileBuilderOps = TopLevelCodeBuilder.() -> Unit
fun CodeBuilder.file(block: FileBuilderOps) = add(TopLevelCodeBuilder().apply(block))