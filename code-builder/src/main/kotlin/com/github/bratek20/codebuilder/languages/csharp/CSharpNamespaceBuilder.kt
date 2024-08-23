package com.github.bratek20.codebuilder.languages.csharp

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.*

class CSharpNamespaceBuilder: TopLevelCodeBuilder() {
    lateinit var name: String

    override fun beforeOperations(): CodeBuilderOps = {
        line("namespace $name {")
        tab()
    }

    override fun afterOperations(): CodeBuilderOps = {
        untab()
        line("}")
    }

    companion object {
        fun create(block: CSharpNamespaceBuilderOps): CSharpNamespaceBuilder {
            return CSharpNamespaceBuilder().apply(block)
        }
    }
}
typealias CSharpNamespaceBuilderOps = CSharpNamespaceBuilder.() -> Unit