package com.some.pkg.somemodule.context

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule

import com.some.pkg.somemodule.api.*
import com.some.pkg.somemodule.impl.*

class SomeModuleImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(SomeInterface::class.java, SomeInterfaceLogic::class.java)
    }
}