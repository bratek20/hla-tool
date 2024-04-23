package pl.bratek20.hla.parsing.impl

import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.directory.impl.DirectoriesLogic
import pl.bratek20.hla.generation.api.ModuleName
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.parsing.api.HlaModulesParser

class HlaModulesParserImpl: HlaModulesParser {
    override fun parse(path: Path): List<HlaModule> {
        val directories = DirectoriesLogic()

        val modulesDir = directories.readDirectory(path)
        return modulesDir.files.map {
            HlaModule(
                name = ModuleName(it.name.split(".module").get(0)),
                simpleValueObjects = emptyList(),
                complexValueObjects = emptyList(),
                interfaces = emptyList()
            )
        }
    }
}