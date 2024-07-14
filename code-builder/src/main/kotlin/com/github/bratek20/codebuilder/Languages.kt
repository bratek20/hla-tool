package com.github.bratek20.codebuilder

enum class BaseType {
    INT, STRING, BOOLEAN
}

interface CodeBuilderLanguage {
    fun name(): String
    fun implements(): String
    fun methodDeclarationKeyword(): String
    fun functionDeclarationKeyword(): String

    fun mapBaseType(type: BaseType): String
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

    override fun mapBaseType(type: BaseType): String {
        return when (type) {
            BaseType.INT -> "Int"
            BaseType.STRING -> "String"
            BaseType.BOOLEAN -> "Boolean"
        }
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

    override fun mapBaseType(type: BaseType): String {
        return when (type) {
            BaseType.INT -> "number"
            BaseType.STRING -> "string"
            BaseType.BOOLEAN -> "boolean"
        }
    }
}