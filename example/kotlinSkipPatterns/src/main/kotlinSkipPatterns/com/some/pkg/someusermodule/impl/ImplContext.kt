package src.main.kotlinSkipPatterns.com.some.pkg.someusermodule.impl

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import src.main.kotlinSkipPatterns.com.some.pkg.someusermodule.api.*
import src.main.kotlinSkipPatterns.com.some.pkg.someusermodule.impl.*

class SomeUserModuleImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(SomeUserInterface::class.java, SomeUserInterfaceLogic::class.java)
    }
}