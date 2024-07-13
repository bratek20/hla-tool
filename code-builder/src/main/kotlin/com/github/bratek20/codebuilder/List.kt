package com.github.bratek20.codebuilder

class ListFieldDeclaration(
    private val fieldName: String,
    private val fieldElementType: String
): BaseCodeBlockBuilder() {
    override fun apply(b: CodeBuilder) {
        if (lang is TypeScript)
            b.line("private readonly $fieldName: $fieldElementType[] = []")
        else
            b.line("private val $fieldName = mutableListOf<$fieldElementType>()")
    }
}