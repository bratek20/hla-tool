package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.TypeBuilder

class InterfaceBuilder: CodeBlockBuilder {
    lateinit var name: String

    private val methods: MutableList<MethodBuilderOps> = mutableListOf()
    fun addMethod(block: MethodBuilderOps) {
        methods.add(block)
    }

    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        val x = c.lang.defaultClassAccessor() + "interface " + name
        line("$x {")
        
        tab()
        methods.forEach { methodOps ->
            method(methodOps)
        }
        untab()
        
        line("}")
    }
}
typealias InterfaceBuilderOps = InterfaceBuilder.() -> Unit
fun CodeBuilder.interfaceBlock(block: InterfaceBuilderOps) = add(InterfaceBuilder().apply(block))