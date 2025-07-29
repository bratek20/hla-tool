package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.Kotlin

fun forLoop(
    from: ExpressionBuilder,
    to: ExpressionBuilder,
    body: (iVar: ExpressionBuilder) -> StatementBuilder
) = statement { c -> {
    if (c.lang is Kotlin) {
        line("for (i in ${from.build(c)} until ${to.build(c)}) {")
    }
    else {
        line("for (let i = ${from.build(c)}; i < ${to.build(c)}; i++) {")
    }
    tab()
    add(body(variable("i")))
    untab()
    line("}")
}}