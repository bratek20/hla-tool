package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.builders.expression

fun mapType(keyType: TypeBuilder, valueType: TypeBuilder) = typeName { c ->
    c.lang.mapType(keyType.build(c), valueType.build(c))
}

fun emptyMap(keyType: TypeBuilder, valueType: TypeBuilder) = expression { c ->
    c.lang.newEmptyMap(keyType.build(c), valueType.build(c))
}

fun newMapOf(keyType: TypeBuilder, valueType: TypeBuilder, vararg pairs: Pair<ExpressionBuilder, ExpressionBuilder>) = expression { c ->
    val pairsStr = pairs.joinToString(", ") { (k, v) -> "${k.build(c)} to ${v.build(c)}" }
    if (pairs.isEmpty()) {
        c.lang.newEmptyMap(keyType.build(c), valueType.build(c))
    } else {
        c.lang.mapOf(pairsStr)
    }
}

class MapOperations(
    private val variable: ExpressionBuilder
) {
    fun get(key: ExpressionBuilder): ExpressionBuilder {
        return expression { c ->
            c.lang.mapGet(variable.build(c), key.build(c))
        }
    }

    fun put(key: ExpressionBuilder, value: ExpressionBuilder): ExpressionBuilder {
        return expression { c ->
            c.lang.mapPut(variable.build(c), key.build(c), value.build(c))
        }
    }

    fun mapValues(transform: (ExpressionBuilder, ExpressionBuilder) -> ExpressionBuilder): ExpressionBuilder {
        return expression { c ->
            val keyParam = c.lang.lambdaParam("key")
            val valueParam = c.lang.lambdaParam("value")
            val keyExpr = expression { keyParam }
            val valueExpr = expression { valueParam }
            val transformResult = transform(keyExpr, valueExpr).build(c)

            c.lang.mapMapValues(variable.build(c), keyParam, valueParam, transformResult)
        }
    }

    fun size(): ExpressionBuilder {
        return expression { c ->
            c.lang.mapSize(variable.build(c))
        }
    }
}

fun mapOp(variable: ExpressionBuilder): MapOperations {
    return MapOperations(variable)
}
