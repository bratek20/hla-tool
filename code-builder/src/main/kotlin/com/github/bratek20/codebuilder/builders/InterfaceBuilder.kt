package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*

class InterfaceBuilder: CodeBlockBuilder {
    lateinit var name: String

    private val methods: MutableList<InterfaceMethodBuilderOps> = mutableListOf()
    fun addMethod(block: InterfaceMethodBuilderOps) {
        methods.add(block)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val x = c.lang.defaultTopLevelAccessor() + "interface " + name
        line("$x {")
        
        tab()
        methods.forEach { methodOps ->
            add(InterfaceMethodBuilder.create(methodOps))
        }
        untab()
        
        line("}")
    }
}
typealias InterfaceBuilderOps = InterfaceBuilder.() -> Unit
fun CodeBuilder.interfaceBlock(block: InterfaceBuilderOps) = add(InterfaceBuilder().apply(block))