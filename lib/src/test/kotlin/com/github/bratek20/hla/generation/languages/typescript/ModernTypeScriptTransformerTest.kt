package com.github.bratek20.hla.generation.languages.typescript

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.definitions.fixtures.moduleDefinition
import com.github.bratek20.hla.facade.api.HlaPaths
import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.HlaSrcPaths
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.facade.api.SubmodulePath
import com.github.bratek20.hla.facade.api.TypeScriptConfig
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.languages.typescript.ModernTypeScriptFile
import com.github.bratek20.hla.generation.impl.languages.typescript.ModernTypeScriptTransformer
import com.github.bratek20.hla.parsing.api.GroupName
import com.github.bratek20.hla.parsing.api.ModuleGroup
import com.github.bratek20.hla.queries.api.BaseModuleGroupQueries
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.context.TypesWorldImpl
import com.github.bratek20.hla.typesworld.fixtures.worldClassType
import com.github.bratek20.utils.directory.api.FileContent
import com.github.bratek20.utils.directory.api.Path
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ModernTypeScriptTransformerTest {
    private lateinit var transformer: ModernTypeScriptTransformer

    private val profile = HlaProfile.create(
        name = ProfileName("typeScriptModern"),
        language = ModuleLanguage.TYPE_SCRIPT,
        paths = HlaPaths.create(
            project = Path("../typescript-modern"),
            src = HlaSrcPaths.create(
                default = Path("main"),
                overrides = listOf(
                    SubmodulePath.create(
                        path = Path("Tests"),
                        submodules = listOf(SubmoduleName.Tests, SubmoduleName.Fixtures)
                    )
                )
            )
        ),
        typeScript = TypeScriptConfig.create(modern = true),
    )

    @BeforeEach
    fun setUp() {
        val typesWorldApi = someContextBuilder()
            .withModules(TypesWorldImpl())
            .build()
            .get(TypesWorldApi::class.java)

        // Both types exist in the world; only ModernModule is generated as an ES module.
        typesWorldApi.addClassType(worldClassType {
            type = {
                name = "ModernClass"
                path = "ModernModule/Api/ValueObjects"
            }
        })
        typesWorldApi.addClassType(worldClassType {
            type = {
                name = "LegacyClass"
                path = "LegacyModule/Api/ValueObjects"
            }
        })

        val group = ModuleGroup.create(
            name = GroupName("hla"),
            modules = listOf(
                moduleDefinition {
                    name = "ModernModule"
                    typeScriptConfig = { modern = true }
                },
                moduleDefinition {
                    name = "LegacyModule"
                }
            ),
            profile = profile,
        )

        transformer = ModernTypeScriptTransformer(profile, BaseModuleGroupQueries(group), typesWorldApi)
    }

    private fun transform(self: ModernTypeScriptFile, vararg lines: String): List<String> {
        return transformer.transform(FileContent(lines.toList()), self).lines
    }

    private fun file(module: String, submodule: SubmoduleName, pattern: PatternName) =
        ModernTypeScriptFile(ModuleName(module), submodule, pattern)

    @Test
    fun `should import a type owned by another modern module`() {
        val result = transform(
            file("ModernModule", SubmoduleName.Impl, PatternName.Logic),
            "class SomeLogic {",
            "    private value: ModernClass",
            "}"
        )

        assertThat(result).contains("import { ModernClass } from \"../Api/ValueObjects\"")
    }

    @Test
    fun `should leave a type owned by a legacy module as an ambient global`() {
        val result = transform(
            file("ModernModule", SubmoduleName.Impl, PatternName.Logic),
            "class SomeLogic {",
            "    private value: LegacyClass",
            "}"
        )

        assertThat(result).noneMatch { it.startsWith("import ") }
        assertThat(result).contains("    private value: LegacyClass")
    }

    @Test
    fun `should not rewrite qualified references into a legacy module`() {
        val result = transform(
            file("ModernModule", SubmoduleName.Fixtures, PatternName.Builders),
            "namespace ModernModule.Builder {",
            "    export function modernClass(): ModernClass {",
            "        return LegacyModule.Builder.legacyClass()",
            "    }",
            "}"
        )

        assertThat(result).noneMatch { it.contains("LegacyModuleBuilder") }
        assertThat(result).contains("    return LegacyModule.Builder.legacyClass()")
    }

    @Test
    fun `should still unwrap the namespace and export declarations of a modern module`() {
        val result = transform(
            file("ModernModule", SubmoduleName.Impl, PatternName.ImplContext),
            "namespace ModernModule.Api {",
            "    export function someMethod(): void {",
            "    }",
            "}"
        )

        assertThat(result).contains("export function someMethod(): void {")
        assertThat(result).noneMatch { it.contains("namespace ") }
    }
}
