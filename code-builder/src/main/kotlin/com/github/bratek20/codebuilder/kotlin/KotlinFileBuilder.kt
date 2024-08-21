package com.github.bratek20.codebuilder.kotlin

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class KotlinFileBuilder: CodeBlockBuilder {
    lateinit var packageName: String

    private val imports: MutableList<String> = mutableListOf()
    private val classes: MutableList<ClassBuilderOps> = mutableListOf()
    private val functions: MutableList<FunctionBuilderOps> = mutableListOf()
    private val enums: MutableList<EnumBuilderOps> = mutableListOf()

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        line("package $packageName")
        if (imports.isNotEmpty()) {
            emptyLine()
            imports.forEach {
                line("import $it")
            }
        }

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
    }

    fun addImport(packageName: String) {
        imports.add(packageName)
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
}
typealias KotlinFileBuilderOps = KotlinFileBuilder.() -> Unit
fun CodeBuilder.kotlinFile(block: KotlinFileBuilderOps) = add(KotlinFileBuilder().apply(block))