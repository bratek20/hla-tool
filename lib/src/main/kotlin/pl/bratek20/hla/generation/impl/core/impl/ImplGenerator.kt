package pl.bratek20.hla.generation.impl.core.impl

import pl.bratek20.hla.definitions.api.ComplexStructureDefinition
import pl.bratek20.hla.definitions.api.KeyDefinition
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator
import pl.bratek20.hla.generation.impl.core.GeneratorMode
import pl.bratek20.hla.generation.impl.core.api.DataClassApiType
import pl.bratek20.hla.generation.impl.core.api.DataClassesGenerator
import pl.bratek20.hla.generation.impl.core.api.InterfaceViewFactory
import pl.bratek20.hla.generation.impl.core.api.PropertyOrDataKeysGenerator

class LogicGenerator: FileGenerator() {
    override fun name(): String {
        return "Logic"
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun generateFileContent(): FileContent? {
        if (module.interfaces.isEmpty()) {
            return null
        }

        val factory = InterfaceViewFactory(apiTypeFactory)

        return contentBuilder("logic.vm")
            .put("interfaces", factory.create(module.interfaces))
            .build()
    }
}

class ImplDataClassesGenerator: DataClassesGenerator() {
    override fun generateFileContent(): FileContent? {
        val content = super.generateFileContent() ?: return null
        val lines = content.lines.toMutableList()
        lines.forEachIndexed { index, line ->
            if (line.contains("class")) {
                lines[index] = line.replace("class", "export class")
            }
            if (line.isNotEmpty()) {
                lines[index] = "    ${lines[index]}"
            }
        }
        lines.add(0, "namespace ${module.name.value}.Impl {")
        lines.add("}")
        return FileContent(lines)
    }

    override fun velocityPathOverride(): String? {
        return "api"
    }

    override fun dataClasses(): List<ComplexStructureDefinition> {
        return module.implSubmodule.data
    }
}

class ImplDataKeysGenerator(): PropertyOrDataKeysGenerator(true) {
    override fun generateFileContent(): FileContent? {
        val content = super.generateFileContent() ?: return null
        val lines = content.lines.toMutableList()
        lines[0] = "namespace ${module.name.value}.Impl {"
        return FileContent(lines)
    }

    override fun velocityPathOverride(): String? {
        return "api"
    }

    override fun dataKeys(): List<KeyDefinition> {
        return module.implSubmodule.dataKeys
    }
}

class ImplGenerator: DirectoryGenerator() {
    override fun name(): String {
        return "Impl"
    }

    override fun velocityDirPath(): String {
        return "impl"
    }

    override fun shouldGenerateDirectory(): Boolean {
        val generateLogic = module.interfaces.isNotEmpty()
        val generateData = module.implSubmodule.data.isNotEmpty()
        return generateLogic || generateData
    }

    override fun getFileGenerators(): List<FileGenerator> {
        return listOf(
            LogicGenerator(),
            ImplDataClassesGenerator(),
            ImplDataKeysGenerator()
        )
    }
}