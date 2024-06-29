package com.some.pkg.somemodule.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.infrastructure.httpserver.api.WebServerModule

import com.some.pkg.somemodule.api.*
import com.some.pkg.somemodule.web.*

class SomeModuleWebClient(
    private val serverUrl: String = "SOME_SERVER_URL"
): ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImplObject(SomeModuleWebServerUrl::class.java, SomeModuleWebServerUrl(serverUrl))
            .setImpl(SomeInterface::class.java, SomeInterfaceWebClient::class.java)
    }
}

class SomeModuleWebServer: WebServerModule {
    override fun getImpl(): ContextModule {
        return SomeModuleImpl()
    }

    override fun getControllers(): List<Class<*>> {
        return listOf(
            SomeInterfaceController::class.java
        )
    }
}