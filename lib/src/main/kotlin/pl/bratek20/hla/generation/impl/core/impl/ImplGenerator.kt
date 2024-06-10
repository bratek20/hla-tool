package pl.bratek20.hla.generation.impl.core.impl

import pl.bratek20.hla.definitions.api.ComplexStructureDefinition
import pl.bratek20.hla.definitions.api.KeyDefinition
import pl.bratek20.hla.directory.api.FileContent
import pl.bratek20.hla.facade.api.ModuleLanguage
import pl.bratek20.hla.generation.impl.core.DirectoryGenerator
import pl.bratek20.hla.generation.impl.core.FileGenerator
import pl.bratek20.hla.generation.impl.core.GeneratorMode
import pl.bratek20.hla.generation.impl.core.api.DataClassApiType
import pl.bratek20.hla.generation.impl.core.api.DataClassesGenerator
import pl.bratek20.hla.generation.impl.core.api.InterfaceViewFactory
import pl.bratek20.hla.generation.impl.core.api.PropertyOrDataKeysGenerator
import pl.bratek20.hla.generation.impl.languages.kotlin.KotlinSupport

class LogicGenerator: FileGenerator() {
    override fun name(): String {
        return "Logic"
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun generateFileContent(): FileContent? {
        if (module.getInterfaces().isEmpty()) {
            return null
        }

        val factory = InterfaceViewFactory(apiTypeFactory)

        return contentBuilder("logic.vm")
            .put("interfaces", factory.create(module.getInterfaces()))
            .build()
    }
}

class ImplDataClassesGenerator: DataClassesGenerator() {
    override fun generateFileContent(): FileContent? {
        val content = super.generateFileContent() ?: return null
        val lines = content.lines.toMutableList()
        if (language.name() == ModuleLanguage.KOTLIN) {
            lines[0] = lines[0].replace("api", "impl")
            return FileContent(lines)
        }

        lines.forEachIndexed { index, line ->
            if (line.contains("class")) {
                lines[index] = line.replace("class", "export class")
            }
            if (line.isNotEmpty()) {
                lines[index] = "    ${lines[index]}"
            }
        }
        lines.add(0, "namespace ${module.getName().value}.Impl {")
        lines.add("}")
        return FileContent(lines)
    }

    override fun velocityPathOverride(): String? {
        return "api"
    }

    override fun dataClasses(): List<ComplexStructureDefinition> {
        return module.getImplSubmodule().getDataClasses()
    }
}

class ImplDataKeysGenerator(): PropertyOrDataKeysGenerator(true) {
    override fun generateFileContent(): FileContent? {
        val content = super.generateFileContent() ?: return null
        val lines = content.lines.toMutableList()

        if (language.name() == ModuleLanguage.KOTLIN) {
            lines[0] = lines[0].replace("api", "impl")
            return FileContent(lines)
        }

        lines[0] = "namespace ${module.getName().value}.Impl {"
        return FileContent(lines)
    }

    override fun velocityPathOverride(): String? {
        return "api"
    }

    override fun dataKeys(): List<KeyDefinition> {
        return module.getImplSubmodule().getDataKeys()
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
        val generateLogic = module.getInterfaces().isNotEmpty()
        val generateData = module.getImplSubmodule().getDataClasses().isNotEmpty()
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