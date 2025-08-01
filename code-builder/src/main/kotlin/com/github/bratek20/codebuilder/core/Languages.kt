package com.github.bratek20.codebuilder.core

enum class BaseType {
    INT, STRING, BOOL, ANY, VOID, DOUBLE, LONG
}

enum class TypeDeclarationStyle {
    TYPE_FIRST,
    VARIABLE_FIRST
}

enum class AccessModifier {
    PRIVATE, PROTECTED, PUBLIC, INTERNAL
}

interface CodeBuilderLanguage {
    fun name(): String

    fun implements(): String
    fun extends(): String

    fun defaultTopLevelAccessor(): String

    fun enumDeclaration(): String

    fun methodDeclarationKeyword(): String
    fun functionDeclarationKeyword(): String

    fun mapBaseType(type: BaseType): String

    fun pairType(firstType: String, secondType: String): String
    fun newPair(first: String, second: String): String
    fun pairFirst(variableName: String): String
    fun pairSecond(variableName: String): String

    fun listType(elementType: String): String
    fun mutableListType(elementType: String): String
    fun newEmptyList(elementType: String): String
    fun newEmptyMutableList(elementType: String): String
    fun listAddCallName(): String
    fun listFindBegin(): String
    fun listFindEnd(): String
    fun listMapBegin(): String
    fun listMapEnd(): String
    fun listSize(): String

    fun softOptionalType(elementType: String): String
    fun hardOptionalType(elementType: String): String
    fun newHardOptional(elementType: String, variableName: String): String
    fun emptyHardOptional(elementType: String): String
    fun optionalGet(variableName: String): String
    fun optionalOrElse(variableName: String, defaultValue: String): String
    fun optionalMapBegin(): String
    fun optionalMapEnd(): String

    fun lambdaArrow(): String

    fun immutableFieldDeclaration(): String
    fun mutableFieldDeclaration(): String

    fun immutableVariableDeclaration(): String
    fun mutableVariableDeclaration(): String

    fun constructorCall(className: String): String

    fun statementTerminator(): String

    fun supportsStaticKeyword(): Boolean

    fun typeDeclarationStyle(): TypeDeclarationStyle

    fun defaultAccessModifierForTopLevelTypes(): AccessModifier
    fun defaultAccessModifierForClassMembers(): AccessModifier

    fun supportsFieldTypeDeductionFromAssignedValue(): Boolean
    fun supportsFieldDeclarationInConstructor(): Boolean

    fun softThis(): String

    fun areMethodsPascalCase(): Boolean = false

    fun nullValue(): String = "null"
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

    override fun defaultTopLevelAccessor(): String {
        return ""
    }

    override fun enumDeclaration(): String {
        return "enum class "
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
            BaseType.BOOL -> "Boolean"
            BaseType.ANY -> "Any"
            BaseType.VOID -> "Unit"
            BaseType.DOUBLE -> "Double"
            BaseType.LONG -> "Long"
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

    override fun newEmptyList(elementType: String): String {
        return "emptyList()"
    }

    override fun listAddCallName(): String {
        return "add"
    }

    override fun listFindBegin(): String {
        return "find { "
    }

    override fun listFindEnd(): String {
        return " }"
    }

    override fun listMapBegin(): String {
        return "map { "
    }

    override fun listMapEnd(): String {
        return " }"
    }

    override fun listSize(): String {
        return "size"
    }

    override fun softOptionalType(elementType: String): String {
        return "$elementType?"
    }

    override fun hardOptionalType(elementType: String): String {
        return "$elementType?"
    }

    override fun newHardOptional(elementType: String, variableName: String): String {
        return variableName
    }

    override fun emptyHardOptional(elementType: String): String {
        return "null"
    }

    override fun optionalGet(variableName: String): String {
        return "${variableName}!!"
    }

    override fun optionalOrElse(variableName: String, defaultValue: String): String {
        return "$variableName ?: $defaultValue"
    }

    override fun optionalMapBegin(): String {
        return "?.let { "
    }

    override fun optionalMapEnd(): String {
        return " }"
    }

    override fun lambdaArrow(): String {
        return "->"
    }

    override fun newEmptyMutableList(elementType: String): String {
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

    override fun supportsStaticKeyword(): Boolean {
        return false
    }

    override fun typeDeclarationStyle(): TypeDeclarationStyle {
        return TypeDeclarationStyle.VARIABLE_FIRST
    }

    override fun defaultAccessModifierForTopLevelTypes(): AccessModifier {
        return AccessModifier.PUBLIC
    }

    override fun defaultAccessModifierForClassMembers(): AccessModifier {
        return AccessModifier.PUBLIC
    }

    override fun supportsFieldTypeDeductionFromAssignedValue(): Boolean {
        return true
    }

    override fun supportsFieldDeclarationInConstructor(): Boolean {
        return true
    }

    override fun softThis(): String {
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

    override fun defaultTopLevelAccessor(): String {
        return ""
    }

    override fun enumDeclaration(): String {
        return "enum "
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
            BaseType.BOOL -> "boolean"
            BaseType.ANY -> "any"
            BaseType.VOID -> "void"
            BaseType.DOUBLE -> "number"
            BaseType.LONG -> "number"
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

    override fun newEmptyList(elementType: String): String {
        return "[]"
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

    override fun listSize(): String {
        return "length"
    }

    override fun softOptionalType(elementType: String): String {
        return "$elementType | undefined"
    }

    override fun hardOptionalType(elementType: String): String {
        return "Optional<$elementType>"
    }

    override fun newHardOptional(elementType: String, variableName: String): String {
        return "Optional.of($variableName)"
    }

    override fun emptyHardOptional(elementType: String): String {
        return "Optional.empty()"
    }

    override fun optionalGet(variableName: String): String {
        return "$variableName.get()"
    }

    override fun optionalOrElse(variableName: String, defaultValue: String): String {
        return "$variableName.orElse($defaultValue)"
    }

    override fun optionalMapBegin(): String {
        return ".map("
    }

    override fun optionalMapEnd(): String {
        return ")"
    }

    override fun lambdaArrow(): String {
        return "=>"
    }

    override fun newEmptyMutableList(elementType: String): String {
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

    override fun supportsStaticKeyword(): Boolean {
        return true
    }

    override fun typeDeclarationStyle(): TypeDeclarationStyle {
        return TypeDeclarationStyle.VARIABLE_FIRST
    }

    override fun defaultAccessModifierForTopLevelTypes(): AccessModifier {
        return AccessModifier.PUBLIC
    }

    override fun defaultAccessModifierForClassMembers(): AccessModifier {
        return AccessModifier.PUBLIC
    }

    override fun supportsFieldTypeDeductionFromAssignedValue(): Boolean {
        return true
    }

    override fun supportsFieldDeclarationInConstructor(): Boolean {
        return true
    }

    override fun softThis(): String {
        return "this."
    }

    override fun nullValue(): String {
        return "undefined"
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

    override fun defaultTopLevelAccessor(): String {
        return "public "
    }

    override fun enumDeclaration(): String {
        return "enum "
    }

    override fun methodDeclarationKeyword(): String {
        return ""
    }

    override fun functionDeclarationKeyword(): String {
        return ""
    }

    override fun mapBaseType(type: BaseType): String {
        return when (type) {
            BaseType.INT -> "int"
            BaseType.STRING -> "string"
            BaseType.BOOL -> "bool"
            BaseType.ANY -> "object"
            BaseType.VOID -> "void"
            BaseType.DOUBLE -> "double"
            BaseType.LONG -> "long"
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

    override fun newEmptyList(elementType: String): String {
        return newEmptyMutableList(elementType)
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
        return ").ToList()"
    }

    override fun listSize(): String {
        return "Count"
    }

    override fun optionalGet(variableName: String): String {
        return "$variableName.Get()"
    }

    override fun optionalOrElse(variableName: String, defaultValue: String): String {
        return "$variableName.OrElse($defaultValue)"
    }

    override fun optionalMapBegin(): String {
        return ".Map("
    }

    override fun optionalMapEnd(): String {
        return ")"
    }

    override fun softOptionalType(elementType: String): String {
        return "$elementType?"
    }

    override fun hardOptionalType(elementType: String): String {
        return "Optional<$elementType>"
    }

    override fun newHardOptional(elementType: String, variableName: String): String {
        return "Optional<$elementType>.Of($variableName)"
    }

    override fun emptyHardOptional(elementType: String): String {
        return "Optional<$elementType>.Empty()"
    }

    override fun lambdaArrow(): String {
        return "=>"
    }

    override fun newEmptyMutableList(elementType: String): String {
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

    override fun supportsStaticKeyword(): Boolean {
        return true
    }

    override fun typeDeclarationStyle(): TypeDeclarationStyle {
        return TypeDeclarationStyle.TYPE_FIRST
    }

    override fun defaultAccessModifierForTopLevelTypes(): AccessModifier {
        return AccessModifier.INTERNAL
    }

    override fun defaultAccessModifierForClassMembers(): AccessModifier {
        return AccessModifier.PRIVATE
    }

    override fun supportsFieldTypeDeductionFromAssignedValue(): Boolean {
        return false
    }

    override fun supportsFieldDeclarationInConstructor(): Boolean {
        return false
    }

    override fun softThis(): String {
        return ""
    }

    override fun areMethodsPascalCase(): Boolean {
        return true
    }
}