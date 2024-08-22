package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

open class FileBuilder: CodeBlockBuilder {
    private val classes: MutableList<ClassBuilderOps> = mutableListOf()
    private val functions: MutableList<FunctionBuilderOps> = mutableListOf()
    private val enums: MutableList<EnumBuilderOps> = mutableListOf()
    private val functionCalls: MutableList<FunctionCallBuilderOps> = mutableListOf()

    protected open fun beforeOperations(): CodeBuilderOps = {}
    protected open fun afterOperations(): CodeBuilderOps = {}

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        add(beforeOperations())

        if (classes.isNotEmpty()) {
            classes.forEach {
                emptyLine()
                classBlock(it)
            }
        }

        if (functions.isNotEmpty()) {
            functions.forEach {
                emptyLine()
                function(it)
            }
        }

        if (enums.isNotEmpty()) {
            enums.forEach {
                emptyLine()
                enum(it)
            }
        }

        if (functionCalls.isNotEmpty()) {
            functionCalls.forEach {
                emptyLine()
                functionCall(it)
            }
        }

        add(afterOperations())
    }

    fun addClass(classBlock: ClassBuilderOps) {
        classes.add(classBlock)
    }

    fun addFunction(function: FunctionBuilderOps) {
        functions.add(function)
    }

    fun addEnum(enumBlock: EnumBuilderOps) {
        enums.add(enumBlock)
    }

    fun addFunctionCall(functionCall: FunctionCallBuilderOps) {
        functionCalls.add(functionCall)
    }
}
typealias FileBuilderOps = FileBuilder.() -> Unit
fun CodeBuilder.file(block: FileBuilderOps) = add(FileBuilder().apply(block))