package com.github.bratek20.hla.validations.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import com.github.bratek20.hla.validations.api.*
import com.github.bratek20.hla.validations.impl.*

class ValidationsImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(HlaValidator::class.java, HlaValidatorLogic::class.java)
    }
}