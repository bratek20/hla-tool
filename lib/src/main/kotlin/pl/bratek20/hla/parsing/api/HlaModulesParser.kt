package pl.bratek20.hla.parsing.api

import pl.bratek20.hla.directory.api.Path
import pl.bratek20.hla.model.HlaModule

interface HlaModulesParser {
    fun parse(path: Path): List<HlaModule>
}