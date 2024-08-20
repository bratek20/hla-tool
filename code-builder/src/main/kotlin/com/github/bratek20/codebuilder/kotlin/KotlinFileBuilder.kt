package com.github.bratek20.codebuilder.kotlin

import com.github.bratek20.codebuilder.builders.ClassBuilderOps
import com.github.bratek20.codebuilder.builders.FunctionBuilderOps
import com.github.bratek20.codebuilder.builders.classBlock
import com.github.bratek20.codebuilder.builders.function
import com.github.bratek20.codebuilder.core.CodeBlockBuilder
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class KotlinFileBuilder: CodeBlockBuilder {
    lateinit var packageName: String

    private val imports: MutableList<String> = mutableListOf()
    private val classes: MutableList<ClassBuilderOps> = mutableListOf()
    private val functions: MutableList<FunctionBuilderOps> = mutableListOf()

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        line("package $packageName")
        emptyLine()
        imports.forEach {
            line("import $it")
        }
        emptyLine()
        classes.forEach {
            classBlock(it)
        }
        emptyLine()
        functions.forEach {
            function(it)
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
}
typealias KotlinFileBuilderOps = KotlinFileBuilder.() -> Unit
fun CodeBuilder.kotlinFile(block: KotlinFileBuilderOps) = add(KotlinFileBuilder().apply(block))