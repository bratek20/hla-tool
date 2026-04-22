package com.some.pkg.webnowrappers.impl

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import com.some.pkg.webnowrappers.api.*
import com.some.pkg.webnowrappers.impl.*

class WebNoWrappersImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(WebApi::class.java, WebApiLogic::class.java)
    }
}