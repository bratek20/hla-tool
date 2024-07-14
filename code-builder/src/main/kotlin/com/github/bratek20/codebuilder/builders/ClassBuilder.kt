package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.*

class FieldBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    lateinit var name: String
    lateinit var type: TypeBuilderOps

    var value: String? = null

    override fun applyOperations(b: CodeBuilder) {
        b.linePart("val $name: ")
        b.add(TypeBuilder(lang).apply(type))
        value?.let {
            b.linePart(" = $it")
        }
    }
}

class ClassBuilder(lang: CodeBuilderLanguage): LangCodeBlockBuilder(lang) {
    var name: String = "SomeClass"
    var implementedInterfaceName: String? = null

    private val body: MutableList<CodeBlockBuilder> = mutableListOf()

    fun method(block: MethodBuilder.() -> Unit) {
        body.add(MethodBuilder(lang).apply(block))
    }

    fun comment(value: String) {
        body.add(OneLineBlock("// $value"))
    }

    fun field(block: FieldBuilder.() -> Unit) {
        body.add(FieldBuilder(lang).apply(block))
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