package pl.bratek20.hla.generation.impl.core

import pl.bratek20.hla.directory.api.Directory
import pl.bratek20.hla.directory.api.File
import pl.bratek20.hla.generation.impl.core.domain.HlaModules
import pl.bratek20.hla.generation.impl.core.domain.ModuleName
import pl.bratek20.hla.velocity.api.VelocityFacade

data class ModulePartGeneratorContext(
    val moduleName: ModuleName,
    val modules: HlaModules,
    val velocity: VelocityFacade
)
abstract class ModulePartGenerator(
    private val c: ModulePartGeneratorContext
) {
    protected val moduleName
        get() = c.moduleName

    protected val module
        get() = c.modules.get(c.moduleName)

    protected val modules
        get() = c.modules

    protected val velocity
        get() = c.velocity
}

abstract class ModulePartFileGenerator(c: ModulePartGeneratorContext)
    : ModulePartGenerator(c)
{
    abstract fun generateFile(): File
}

abstract class ModulePartDirectoryGenerator(c: ModulePartGeneratorContext)
    : ModulePartGenerator(c)
{
    abstract fun generateDirectory(): Directory
}