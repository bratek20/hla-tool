package com.some.pkg.somemodule.context

import com.some.pkg.somemodule.api.SomeInterface
import com.some.pkg.somemodule.impl.SomeInterfaceImpl
import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule

class SomeModuleImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(SomeInterface::class.java, SomeInterfaceImpl::class.java)
    }
}