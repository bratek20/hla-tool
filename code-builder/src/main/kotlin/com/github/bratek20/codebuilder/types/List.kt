package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.*

class ListFieldDeclaration(
    private val fieldName: String,
    private val fieldElementType: String
): CodeBlockBuilder {
    override fun getOperations(c: CodeBuilderContext): CodeBuilderOps = {
        if (c.lang is TypeScript)
            line("private readonly $fieldName: $fieldElementType[] = []")
        else
            line("private val $fieldName = mutableListOf<$fieldElementType>()")
    }
}