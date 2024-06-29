package com.some.pkg.somemodule.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.infrastructure.httpserver.api.WebServerModule

class SomeModuleWebClient: ContextModule {
    override fun apply(builder: ContextBuilder) {
        TODO("Not yet implemented")
    }
}

class SomeModuleWebServer: WebServerModule {
    override fun getControllers(): List<Class<*>> {
        TODO("Not yet implemented")
    }

    override fun getImpl(): ContextModule {
        TODO("Not yet implemented")
    }
}