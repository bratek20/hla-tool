package com.github.bratek20.hla.facade.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.architecture.properties.context.PropertiesImpl
import com.github.bratek20.architecture.properties.sources.yaml.YamlPropertiesSourceImpl
import com.github.bratek20.hla.facade.api.HlaFacade
import com.github.bratek20.hla.facade.impl.HlaFacadeLogic
import com.github.bratek20.hla.generation.context.GenerationImpl
import com.github.bratek20.hla.velocity.context.VelocityImpl
import com.github.bratek20.hla.writing.context.WritingImpl

class FacadeImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(HlaFacade::class.java, HlaFacadeLogic::class.java)
            .withModules(
                PropertiesImpl(),
                YamlPropertiesSourceImpl(),

                GenerationImpl(),
                WritingImpl()
            )
    }
}