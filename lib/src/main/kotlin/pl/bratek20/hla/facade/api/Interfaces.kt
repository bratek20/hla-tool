package pl.bratek20.hla.facade.api

interface HlaFacade {
    fun startModule(args: ModuleOperationArgs): Unit

    fun updateModule(args: ModuleOperationArgs): Unit
}