package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*

class ClassBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    var name: String = "SomeClass"
    var implementedInterfaceName: String? = null
    var body: CodeBlockBuilder = EmptyBlock()

    private val methods: MutableList<MethodBuilder> = mutableListOf()

    fun addMethod(block: MethodBuilder.() -> Unit) {
        methods.add(MethodBuilder(lang).apply(block))
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
        b.add(body)
        methods.forEach { b.add(it) }
    }
}
typealias ClassBuilderOps = ClassBuilder.() -> Unit