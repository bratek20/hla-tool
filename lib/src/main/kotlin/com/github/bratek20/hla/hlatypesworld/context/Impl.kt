package com.github.bratek20.hla.hlatypesworld.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import com.github.bratek20.hla.hlatypesworld.api.*
import com.github.bratek20.hla.hlatypesworld.impl.*

class HlaTypesWorldImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(HlaTypesWorldApi::class.java, HlaTypesWorldApiLogic::class.java)
            .setImpl(HlaTypesWorldQueries::class.java, HlaTypesWorldQueriesLogic::class.java)
            .setImpl(HlaTypesExtraInfo::class.java, HlaTypesExtraInfoLogic::class.java)
            .withModule(HlaTypesWorldPopulators())
    }
}

class HlaTypesWorldPopulators: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .addImpl(HlaTypesWorldPopulator::class.java, PrimitiveTypesPopulator::class.java)
            .addImpl(HlaTypesWorldPopulator::class.java, B20FrontendTypesPopulator::class.java)
            .addImpl(HlaTypesWorldPopulator::class.java, ApiTypesPopulator::class.java)
            .addImpl(HlaTypesWorldPopulator::class.java, ViewModelTypesPopulator::class.java)
            .addImpl(HlaTypesWorldPopulator::class.java, ViewTypesPopulator::class.java)
    }
}