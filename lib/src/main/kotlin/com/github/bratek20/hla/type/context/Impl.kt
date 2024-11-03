package com.github.bratek20.hla.type.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import com.github.bratek20.hla.type.api.*
import com.github.bratek20.hla.type.impl.*

class TypeImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(TypeApi::class.java, TypeApiLogic::class.java)
    }
}