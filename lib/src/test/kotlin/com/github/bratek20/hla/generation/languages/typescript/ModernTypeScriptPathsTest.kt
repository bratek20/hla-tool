package com.github.bratek20.hla.generation.languages.typescript

import com.github.bratek20.hla.facade.api.HlaPaths
import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.HlaSrcPaths
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.facade.api.SubmodulePath
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.languages.typescript.ModernTypeScriptFile
import com.github.bratek20.hla.generation.impl.languages.typescript.ModernTypeScriptPaths
import com.github.bratek20.utils.directory.api.Path
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ModernTypeScriptPathsTest {
    // Mirrors the typeScriptModern profile from example/hla/properties.yaml
    private val profile: HlaProfile = HlaProfile.create(
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
                    ),
                    SubmodulePath.create(
                        path = Path("Examples"),
                        submodules = listOf(SubmoduleName.Examples)
                    ),
                )
            )
        ),
        typeScript = null,
    )

    private val paths = ModernTypeScriptPaths(profile)

    private fun file(module: String, submodule: SubmoduleName, pattern: PatternName) =
        ModernTypeScriptFile(ModuleName(module), submodule, pattern)

    @Test
    fun `should point to a sibling submodule of the same module`() {
        val from = file("SomeModule", SubmoduleName.Impl, PatternName.Logic)
        val to = file("SomeModule", SubmoduleName.Api, PatternName.ValueObjects)

        assertThat(paths.calculateImportPath(from, to)).isEqualTo("../Api/ValueObjects")
    }

    @Test
    fun `should point to a file in the same directory`() {
        val from = file("SomeModule", SubmoduleName.Api, PatternName.DataKeys)
        val to = file("SomeModule", SubmoduleName.Api, PatternName.DataClasses)

        assertThat(paths.calculateImportPath(from, to)).isEqualTo("./DataClasses")
    }

    @Test
    fun `should point to another module`() {
        val from = file("SomeModule", SubmoduleName.Api, PatternName.ValueObjects)
        val to = file("OtherModule", SubmoduleName.Api, PatternName.ValueObjects)

        assertThat(paths.calculateImportPath(from, to)).isEqualTo("../../OtherModule/Api/ValueObjects")
    }

    @Test
    fun `should cross from tests root back to main root`() {
        val from = file("SomeModule", SubmoduleName.Fixtures, PatternName.Builders)
        val to = file("OtherModule", SubmoduleName.Api, PatternName.ValueObjects)

        assertThat(paths.calculateImportPath(from, to)).isEqualTo("../../../main/OtherModule/Api/ValueObjects")
    }

    @Test
    fun `should reach fixtures from tests within the same module`() {
        val from = file("SomeModule", SubmoduleName.Tests, PatternName.TestBase)
        val to = file("SomeModule", SubmoduleName.Fixtures, PatternName.Builders)

        assertThat(paths.calculateImportPath(from, to)).isEqualTo("../Fixtures/Builders")
    }

    @Test
    fun `should use Notifications as the file name of the Events pattern`() {
        val from = file("SomeModule", SubmoduleName.Impl, PatternName.Logic)
        val to = file("SomeModule", SubmoduleName.Api, PatternName.Events)

        assertThat(paths.calculateImportPath(from, to)).isEqualTo("../Api/Notifications")
    }
}
