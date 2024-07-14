package com.github.bratek20.codebuilder.clazz

import com.github.bratek20.codebuilder.BaseCodeBlockBuilder
import com.github.bratek20.codebuilder.CodeBlockBuilder
import com.github.bratek20.codebuilder.CodeBuilder

class ClassBuilder: BaseCodeBlockBuilder() {
    lateinit var name: String
    lateinit var implementedInterfaceName: String
    lateinit var body: CodeBlockBuilder

    //private val methods: MutableList<CodeBlockBuilder> = mutableListOf()

//    fun field(block: CodeBlockBuilder.() -> Unit) {
//        fields.add(FieldBuilder().apply(block))
//    }

    override fun apply(b: CodeBuilder) {
        b.line("class $name${lang.implements()}$implementedInterfaceName {")
        b.tab()
        body.apply(b)
        b.untab()
        b.line("}")
    }
}