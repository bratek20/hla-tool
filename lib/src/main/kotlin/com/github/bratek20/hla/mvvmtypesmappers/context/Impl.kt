package com.github.bratek20.hla.mvvmtypesmappers.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import com.github.bratek20.hla.mvvmtypesmappers.api.*
import com.github.bratek20.hla.mvvmtypesmappers.impl.*

class MvvmTypesMappersImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(ViewModelToViewMapper::class.java, ViewModelToViewMapperLogic::class.java)
    }
}