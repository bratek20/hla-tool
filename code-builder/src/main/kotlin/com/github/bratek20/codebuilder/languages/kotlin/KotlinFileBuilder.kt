package com.github.bratek20.codebuilder.languages.kotlin

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.CodeBuilder
import com.github.bratek20.codebuilder.core.CodeBuilderOps

class KotlinFileBuilder: TopLevelCodeBuilder() {
    lateinit var packageName: String

    private val imports: MutableList<String> = mutableListOf()

    override fun beforeOperations(): CodeBuilderOps = {
        line("package $packageName")
        if (imports.isNotEmpty()) {
            emptyLine()
            imports.forEach {
                line("import $it")
            }
            emptyLine()
        }
    }

    fun addImport(packageName: String) {
        imports.add(packageName)
    }
}
typealias KotlinFileBuilderOps = KotlinFileBuilder.() -> Unit
fun CodeBuilder.kotlinFile(block: KotlinFileBuilderOps) = add(KotlinFileBuilder().apply(block))