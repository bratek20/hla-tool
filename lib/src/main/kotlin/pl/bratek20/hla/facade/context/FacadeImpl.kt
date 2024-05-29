package pl.bratek20.hla.facade.context

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.architecture.properties.context.PropertiesImpl
import pl.bratek20.architecture.properties.sources.yaml.YamlPropertiesSourceImpl
import pl.bratek20.hla.facade.api.HlaFacade
import pl.bratek20.hla.facade.impl.HlaFacadeLogic
import pl.bratek20.hla.generation.context.GenerationImpl
import pl.bratek20.hla.velocity.context.VelocityImpl
import pl.bratek20.hla.writing.context.WritingImpl
import pl.bratek20.utils.logs.context.LoggerImpl

class FacadeImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(HlaFacade::class.java, HlaFacadeLogic::class.java)
            .withModules(
                LoggerImpl(),

                PropertiesImpl(),
                YamlPropertiesSourceImpl(),

                VelocityImpl(),

                GenerationImpl(),
                WritingImpl()
            )
    }
}