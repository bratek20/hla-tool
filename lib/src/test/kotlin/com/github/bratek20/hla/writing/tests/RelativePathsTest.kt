package com.github.bratek20.hla.writing.tests

import com.github.bratek20.hla.writing.impl.pathParts
import com.github.bratek20.hla.writing.impl.relativeModuleSpecifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RelativePathsTest {
    @Test
    fun `should go up and down between sibling directories`() {
        val specifier = relativeModuleSpecifier(
            listOf("main", "SomeModule", "Impl"),
            listOf("main", "SomeModule", "Api", "ValueObjects")
        )

        assertThat(specifier).isEqualTo("../Api/ValueObjects")
    }

    @Test
    fun `should prefix with dot slash when target is below the source directory`() {
        val specifier = relativeModuleSpecifier(
            listOf("main"),
            listOf("main", "SomeModule", "Web", "PlayFabHandlers")
        )

        assertThat(specifier).isEqualTo("./SomeModule/Web/PlayFabHandlers")
    }

    @Test
    fun `should cross between src roots`() {
        val specifier = relativeModuleSpecifier(
            listOf("Tests", "SomeModule", "Fixtures"),
            listOf("main", "OtherModule", "Api", "ValueObjects")
        )

        assertThat(specifier).isEqualTo("../../../main/OtherModule/Api/ValueObjects")
    }

    @Test
    fun `should treat an empty directory as the root`() {
        val specifier = relativeModuleSpecifier(
            pathParts(""),
            listOf("main", "SomeModule", "Web", "PlayFabHandlers")
        )

        assertThat(specifier).isEqualTo("./main/SomeModule/Web/PlayFabHandlers")
    }

    @Test
    fun `should drop empty segments when splitting a path`() {
        assertThat(pathParts("")).isEmpty()
        assertThat(pathParts("main")).containsExactly("main")
        assertThat(pathParts("src/main/kotlin")).containsExactly("src", "main", "kotlin")
    }
}
