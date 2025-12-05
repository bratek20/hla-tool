package src.main.kotlinSkipPatterns.com.some.pkg.othermodule.impl

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import src.main.kotlinSkipPatterns.com.some.pkg.othermodule.api.*
import src.main.kotlinSkipPatterns.com.some.pkg.othermodule.impl.*

class OtherModuleImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(OtherInterface::class.java, OtherInterfaceLogic::class.java)
    }
}