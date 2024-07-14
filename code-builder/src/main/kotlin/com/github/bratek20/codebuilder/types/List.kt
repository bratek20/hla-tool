package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.LangCodeBlockBuilder
import com.github.bratek20.codebuilder.CodeBuilder
import com.github.bratek20.codebuilder.CodeBuilderLanguage
import com.github.bratek20.codebuilder.TypeScript

class ListFieldDeclaration(
    lang: CodeBuilderLanguage,
    private val fieldName: String,
    private val fieldElementType: String
): LangCodeBlockBuilder(lang) {
    override fun applyOperations(b: CodeBuilder) {
        if (lang is TypeScript)
            b.line("private readonly $fieldName: $fieldElementType[] = []")
        else
            b.line("private val $fieldName = mutableListOf<$fieldElementType>()")
    }
}