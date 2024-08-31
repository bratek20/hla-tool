package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.expression

fun pairType(first: TypeBuilder, second: TypeBuilder) = typeName { c ->
    c.lang.pairType(first.build(c), second.build(c))
}

fun newPair(first: String, second: String) = expression { c ->
    c.lang.newPair(first, second)
}

class PairOperations(
    private val variableName: String
) {
    fun first() = expression { c ->
        c.lang.pairFirst(variableName)
    }

    fun second() = expression { c ->
        c.lang.pairSecond(variableName)
    }
}

fun pairOp(variableName: String): PairOperations {
    return PairOperations(variableName)
}