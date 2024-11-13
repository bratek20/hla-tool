package com.github.bratek20.hla.typesworld.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import com.github.bratek20.hla.typesworld.api.*
import com.github.bratek20.hla.typesworld.impl.*

class TypesWorldImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(TypesWorldApi::class.java, TypesWorldApiLogic::class.java)
    }
}