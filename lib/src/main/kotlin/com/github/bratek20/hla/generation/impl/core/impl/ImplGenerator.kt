package com.github.bratek20.hla.generation.impl.core.impl

import com.github.bratek20.hla.definitions.api.ComplexStructureDefinition
import com.github.bratek20.hla.definitions.api.KeyDefinition
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.api.patterns.DataClassesGenerator
import com.github.bratek20.hla.generation.impl.core.api.patterns.InterfaceViewFactory
import com.github.bratek20.hla.generation.impl.core.api.PropertyOrDataKeysGenerator
import com.github.bratek20.hla.tracking.impl.InitSqlGenerator
import com.github.bratek20.hla.tracking.impl.TrackPatternGenerator

class LogicGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Logic
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
            val apiPackage = lines[0].removePrefix("package ")

            lines[0] = lines[0].replace("api", "impl")
            lines.add(1, "")
            lines.add(2, "import $apiPackage.*")

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
        return module.getImplSubmodule()?.getDataClasses() ?: emptyList()
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
        return module.getImplSubmodule()?.getDataKeys() ?: emptyList()
    }
}

class ImplGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Impl
    }

    override fun velocityDirPath(): String {
        return "impl"
    }

    override fun shouldGenerateSubmodule(): Boolean {
        val generateLogic = module.getInterfaces().isNotEmpty()
        val generateData = module.getImplSubmodule() != null
        return generateLogic || generateData
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            ImplDataClassesGenerator(),
            ImplDataKeysGenerator(),
            TrackPatternGenerator(),
            InitSqlGenerator(),
            LogicGenerator(),
        )
    }
}