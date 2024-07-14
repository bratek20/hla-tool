package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*

class ClassBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    var name: String = "SomeClass"
    var implementedInterfaceName: String? = null

    private val body: MutableList<CodeBlockBuilder> = mutableListOf()

    fun addMethod(block: MethodBuilder.() -> Unit) {
        body.add(MethodBuilder(lang).apply(block))
    }

    fun addComment(comment: String) {
        body.add(OneLineBlock("// $comment"))
    }

    override fun applyOperations(b: CodeBuilder) {
        val implementsPart = implementedInterfaceName?.let { lang.implements() + it } ?: ""

        b.line("class $name${implementsPart} {")
        b.tab()
        applyBodyOperations(b)
        b.untab()
        b.line("}")
    }

    fun applyBodyOperations(b: CodeBuilder) {
        body.forEach { b.add(it) }
    }
}
typealias ClassBuilderOps = ClassBuilder.() -> Unit