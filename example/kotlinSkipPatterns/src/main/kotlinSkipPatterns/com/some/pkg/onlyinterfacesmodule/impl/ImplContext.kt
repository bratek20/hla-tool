package src.main.kotlinSkipPatterns.com.some.pkg.onlyinterfacesmodule.impl

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import src.main.kotlinSkipPatterns.com.some.pkg.onlyinterfacesmodule.api.*
import src.main.kotlinSkipPatterns.com.some.pkg.onlyinterfacesmodule.impl.*

class OnlyInterfacesModuleImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(OnlyInterfacesModuleInterface::class.java, OnlyInterfacesModuleInterfaceLogic::class.java)
    }
}