package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

open class FileBuilder: CodeBlockBuilder {
    private val ops: MutableList<CodeBuilderOps> = mutableListOf()

    protected open fun beforeOperations(): CodeBuilderOps = {}
    protected open fun afterOperations(): CodeBuilderOps = {}

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        add(beforeOperations())

        ops.forEach {
            emptyLine()
            add(it)
        }

        add(afterOperations())
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
typealias FileBuilderOps = FileBuilder.() -> Unit
fun CodeBuilder.file(block: FileBuilderOps) = add(FileBuilder().apply(block))