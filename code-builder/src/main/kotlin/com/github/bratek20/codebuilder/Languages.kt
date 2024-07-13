package com.github.bratek20.codebuilder

interface CodeBuilderLanguage {
    fun implements(): String
    fun methodDeclarationKeyword(): String
}

class Kotlin: CodeBuilderLanguage {
    override fun implements(): String {
        return ": "
    }

    override fun methodDeclarationKeyword(): String {
        return "fun "
    }
}

class TypeScript: CodeBuilderLanguage {
    override fun implements(): String {
        return " implements "
    }

    override fun methodDeclarationKeyword(): String {
        return ""
    }
}