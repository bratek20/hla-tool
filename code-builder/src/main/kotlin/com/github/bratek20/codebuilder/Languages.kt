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

    fun pairType(firstType: String, secondType: String): String
    fun pairFirst(variableName: String): String
    fun pairSecond(variableName: String): String

    fun listType(elementType: String): String
    fun listAdd(variableName: String, element: String): String
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

    override fun pairType(firstType: String, secondType: String): String {
        return "Pair<$firstType, $secondType>"
    }

    override fun pairFirst(variableName: String): String {
        return "${variableName}.first"
    }

    override fun pairSecond(variableName: String): String {
        return "${variableName}.second"
    }

    override fun listType(elementType: String): String {
        return "List<$elementType>"
    }

    override fun listAdd(variableName: String, element: String): String {
        return "$variableName.add($element)"
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

    override fun pairType(firstType: String, secondType: String): String {
        return "[$firstType, $secondType]"
    }

    override fun pairFirst(variableName: String): String {
        return "${variableName}[0]"
    }

    override fun pairSecond(variableName: String): String {
        return "${variableName}[1]"
    }

    override fun listType(elementType: String): String {
        return "$elementType[]"
    }

    override fun listAdd(variableName: String, element: String): String {
        return "$variableName.push($element)"
    }
}