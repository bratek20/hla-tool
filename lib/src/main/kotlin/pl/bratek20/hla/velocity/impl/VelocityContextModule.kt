package pl.bratek20.hla.velocity.impl

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.velocity.api.VelocityFacade

class VelocityContextModule: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(VelocityFacade::class.java, VelocityFacadeImpl::class.java)
    }
}