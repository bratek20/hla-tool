package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.Kotlin

fun forLoop(
    from: ExpressionBuilder,
    to: ExpressionBuilder,
    indexName: String = "i",
    body: (iVar: ExpressionBuilder) -> StatementBuilder
) = statement { c -> {
    if (c.lang is Kotlin) {
        line("for ($indexName in ${from.build(c)} until ${to.build(c)}) {")
    }
    else {
        line("for (let $indexName = ${from.build(c)}; $indexName < ${to.build(c)}; $indexName++) {")
    }
    tab()
    add(body(variable(indexName)))
    untab()
    line("}")
}}