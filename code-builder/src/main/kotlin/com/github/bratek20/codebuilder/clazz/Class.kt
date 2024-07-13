package com.github.bratek20.codebuilder.clazz

import com.github.bratek20.codebuilder.BaseCodeBlockBuilder
import com.github.bratek20.codebuilder.CodeBlockBuilder
import com.github.bratek20.codebuilder.CodeBuilder

class Class(
    private val className: String,
    private val implementedInterfaceName: String,
    private val body: CodeBlockBuilder
): BaseCodeBlockBuilder() {
    override fun apply(b: CodeBuilder) {
        b.line("class $className${lang.implements()}$implementedInterfaceName {")
        b.tab()
        body.apply(b)
        b.untab()
        b.line("}")
    }
}