package com.github.bratek20.codebuilder

interface CodeBuilderLanguage {
    fun name(): String
    fun implements(): String
    fun methodDeclarationKeyword(): String
    fun functionDeclarationKeyword(): String
}

class Kotlin: CodeBuilderLanguage {
    override fun name(): String {
        return "Kotlin"
    }

    override fun implements(): String {
        return ": "
    }

    override fun methodDeclarationKeyword(): String {
        return "fun "
    }

    override fun functionDeclarationKeyword(): String {
        return "fun "
    }
}

class TypeScript: CodeBuilderLanguage {
    override fun name(): String {
        return "TypeScript"
    }

    override fun implements(): String {
        return " implements "
    }

    override fun methodDeclarationKeyword(): String {
        return ""
    }

    override fun functionDeclarationKeyword(): String {
        return "function "
    }
}