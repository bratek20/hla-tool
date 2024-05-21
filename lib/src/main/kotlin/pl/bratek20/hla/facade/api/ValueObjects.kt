package pl.bratek20.hla.facade.api

import pl.bratek20.hla.directory.api.*

data class ModuleOperationArgs(
    val hlaFolderPath: Path,
    val profileName: ProfileName,
    val moduleName: ModuleName,
)