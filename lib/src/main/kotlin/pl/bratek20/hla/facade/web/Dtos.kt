package pl.bratek20.hla.facade.web

import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.facade.api.GenerateModuleArgs
import pl.bratek20.hla.facade.api.ModuleLanguage
import pl.bratek20.hla.facade.api.ModuleName

data class GenerateModuleArgsDto(
    val moduleName: String,
    val language: String,
    val hlaFolderPath: String,
    val projectPath: String,
) {
    fun toApi(): GenerateModuleArgs {
        return GenerateModuleArgs(
            moduleName = ModuleName(moduleName),
            language = ModuleLanguage.valueOf(language),
            hlaFolderPath = Path(hlaFolderPath),
            projectPath = Path(projectPath),
        )
    }

    companion object {
        fun fromApi(api: GenerateModuleArgs): GenerateModuleArgsDto {
            return GenerateModuleArgsDto(
                moduleName = api.moduleName.value,
                language = api.language.name,
                hlaFolderPath = api.hlaFolderPath.value,
                projectPath = api.projectPath.value,
            )
        }
    }
}