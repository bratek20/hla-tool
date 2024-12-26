package com.github.bratek20.hla.importscalculation.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import com.github.bratek20.hla.importscalculation.api.*
import com.github.bratek20.hla.importscalculation.impl.*

class ImportsCalculationImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(ImportsCalculator::class.java, ImportsCalculatorLogic::class.java)
    }
}