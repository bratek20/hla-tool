package com.github.bratek20.codebuilder

interface CodeBuilderLanguage {
    fun implements(): String
}

class Kotlin: CodeBuilderLanguage {
    override fun implements(): String {
        return ": "
    }
}

class TypeScript: CodeBuilderLanguage {
    override fun implements(): String {
        return " implements "
    }
}