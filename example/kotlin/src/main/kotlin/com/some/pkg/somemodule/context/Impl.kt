package com.some.pkg.somemodule.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import com.some.pkg.somemodule.api.*
import com.some.pkg.somemodule.impl.*

class SomeModuleImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(SomeEmptyInterface::class.java, SomeEmptyInterfaceLogic::class.java)
            .setImpl(SomeInterface::class.java, SomeInterfaceLogic::class.java)
            .setImpl(SomeInterface2::class.java, SomeInterface2Logic::class.java)
            .setImpl(SomeInterface3::class.java, SomeInterface3Logic::class.java)
            .setImpl(TestCustomTypesInterface::class.java, TestCustomTypesInterfaceLogic::class.java)
            .setImpl(SomeModuleHandlers::class.java, SomeModuleHandlersLogic::class.java)
            .setImpl(SomeModuleDebugHandlers::class.java, SomeModuleDebugHandlersLogic::class.java)
    }
}