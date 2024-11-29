package com.github.bratek20.hla.hlatypesworld.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import com.github.bratek20.hla.hlatypesworld.api.*
import com.github.bratek20.hla.hlatypesworld.impl.*

class HlaTypesWorldImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(HlaTypesWorldApi::class.java, HlaTypesWorldApiLogic::class.java)
            .setImpl(HlaTypesWorldPopulator::class.java, HlaTypesWorldPopulatorLogic::class.java)
    }
}