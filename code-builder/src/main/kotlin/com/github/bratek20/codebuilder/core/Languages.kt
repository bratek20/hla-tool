package com.github.bratek20.codebuilder.core

enum class BaseType {
    INT, STRING, BOOLEAN
}

interface CodeBuilderLanguage {
    fun name(): String

    fun implements(): String
    fun extends(): String

    fun defaultClassAccessor(): String

    fun methodDeclarationKeyword(): String
    fun functionDeclarationKeyword(): String

    fun mapBaseType(type: BaseType): String

    fun pairType(firstType: String, secondType: String): String
    fun newPair(first: String, second: String): String
    fun pairFirst(variableName: String): String
    fun pairSecond(variableName: String): String

    fun listType(elementType: String): String
    fun mutableListType(elementType: String): String
    fun emptyMutableList(elementType: String): String
    fun listAddCallName(): String
    fun listFindBegin(): String
    fun listFindEnd(): String
    fun listMapBegin(): String
    fun listMapEnd(): String

    fun lambdaArrow(): String

    fun immutableFieldDeclaration(): String
    fun mutableFieldDeclaration(): String

    fun immutableVariableDeclaration(): String
    fun mutableVariableDeclaration(): String

    fun constructorCall(className: String): String

    fun statementTerminator(): String
}

class Kotlin: CodeBuilderLanguage {
    override fun name(): String {
        return "Kotlin"
    }

    override fun implements(): String {
        return ": "
    }

    override fun extends(): String {
        return ": "
    }

    override fun defaultClassAccessor(): String {
        return ""
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

    override fun newPair(first: String, second: String): String {
        return "Pair($first, $second)"
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

    override fun mutableListType(elementType: String): String {
        return "MutableList<$elementType>"
    }

    override fun listAddCallName(): String {
        return "add"
    }

    override fun listFindBegin(): String {
        return "find {"
    }

    override fun listFindEnd(): String {
        return "}"
    }

    override fun listMapBegin(): String {
        return "map {"
    }

    override fun listMapEnd(): String {
        return "}"
    }

    override fun lambdaArrow(): String {
        return "->"
    }

    override fun emptyMutableList(elementType: String): String {
        return "mutableListOf()"
    }

    override fun immutableFieldDeclaration(): String {
        return "val "
    }

    override fun mutableFieldDeclaration(): String {
        return "var "
    }

    override fun immutableVariableDeclaration(): String {
        return "val "
    }

    override fun mutableVariableDeclaration(): String {
        return "var "
    }

    override fun constructorCall(className: String): String {
        return className
    }

    override fun statementTerminator(): String {
        return ""
    }
}

class TypeScript: CodeBuilderLanguage {
    override fun name(): String {
        return "TypeScript"
    }

    override fun implements(): String {
        return " implements "
    }

    override fun extends(): String {
        return " extends "
    }

    override fun defaultClassAccessor(): String {
        return ""
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

    override fun newPair(first: String, second: String): String {
        return "[$first, $second]"
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

    override fun mutableListType(elementType: String): String {
        return "$elementType[]"
    }

    override fun listAddCallName(): String {
        return "push"
    }

    override fun listFindBegin(): String {
        return "find("
    }

    override fun listFindEnd(): String {
        return ")"
    }

    override fun listMapBegin(): String {
        return "map("
    }

    override fun listMapEnd(): String {
        return ")"
    }

    override fun lambdaArrow(): String {
        return "=>"
    }

    override fun emptyMutableList(elementType: String): String {
        return "[]"
    }

    override fun immutableFieldDeclaration(): String {
        return "readonly "
    }

    override fun mutableFieldDeclaration(): String {
        return ""
    }

    override fun immutableVariableDeclaration(): String {
        return "const "
    }

    override fun mutableVariableDeclaration(): String {
        return "let "
    }

    override fun constructorCall(className: String): String {
        return "new $className"
    }

    override fun statementTerminator(): String {
        return ""
    }
}

class CSharp: CodeBuilderLanguage {
    override fun name(): String {
        return "C#"
    }

    override fun implements(): String {
        return ": "
    }

    override fun extends(): String {
        return ": "
    }

    override fun defaultClassAccessor(): String {
        return "public "
    }

    override fun methodDeclarationKeyword(): String {
        return "public "
    }

    override fun functionDeclarationKeyword(): String {
        return "public "
    }

    override fun mapBaseType(type: BaseType): String {
        return when (type) {
            BaseType.INT -> "int"
            BaseType.STRING -> "string"
            BaseType.BOOLEAN -> "bool"
        }
    }

    override fun pairType(firstType: String, secondType: String): String {
        return "Tuple<$firstType, $secondType>"
    }

    override fun newPair(first: String, second: String): String {
        return "Tuple.Create($first, $second)"
    }

    override fun pairFirst(variableName: String): String {
        return "${variableName}.Item1"
    }

    override fun pairSecond(variableName: String): String {
        return "${variableName}.Item2"
    }

    override fun listType(elementType: String): String {
        return "List<$elementType>"
    }

    override fun mutableListType(elementType: String): String {
        return "List<$elementType>"
    }

    override fun listAddCallName(): String {
        return "Add"
    }

    override fun listFindBegin(): String {
        return "Find("
    }

    override fun listFindEnd(): String {
        return ")"
    }

    override fun listMapBegin(): String {
        return "Select("
    }

    override fun listMapEnd(): String {
        return ")"
    }

    override fun lambdaArrow(): String {
        return "=>"
    }

    override fun emptyMutableList(elementType: String): String {
        return "new List<$elementType>()"
    }

    override fun immutableFieldDeclaration(): String {
        return "readonly "
    }

    override fun mutableFieldDeclaration(): String {
        return ""
    }

    override fun immutableVariableDeclaration(): String {
        return "var "
    }

    override fun mutableVariableDeclaration(): String {
        return "var "
    }

    override fun constructorCall(className: String): String {
        return "new $className"
    }

    override fun statementTerminator(): String {
        return ";"
    }
}