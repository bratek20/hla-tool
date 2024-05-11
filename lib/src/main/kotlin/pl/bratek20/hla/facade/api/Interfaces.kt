package pl.bratek20.hla.facade.api

interface HlaFacade {
    fun generateModule(args: GenerateModuleArgs): Unit
}