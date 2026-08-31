package com.github.bratek20.hla.writing.tests

import com.github.bratek20.hla.writing.impl.addModuleFilesToTsConfig
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TsConfigEditsTest {
    private val apiPaths = listOf("main/SomeModule/Api/ValueObjects.ts")
    private val implPaths = listOf("main/SomeModule/Impl/Logic.ts", "main/SomeModule/Impl/ImplContext.ts")

    private fun addSomeModuleFiles(lines: List<String>): List<String> {
        return addModuleFilesToTsConfig(lines, "SomeModule", listOf(apiPaths, implPaths))
    }

    @Test
    fun `should add a group per submodule at the end of the list`() {
        val given = listOf(
            "{",
            "    \"files\": [",
            "    ]",
            "}"
        )

        val result = addSomeModuleFiles(given)

        assertThat(result).containsExactly(
            "{",
            "    \"files\": [",
            "",
            "        //SomeModule start",
            "        \"main/SomeModule/Api/ValueObjects.ts\",",
            "",
            "        \"main/SomeModule/Impl/Logic.ts\",",
            "        \"main/SomeModule/Impl/ImplContext.ts\",",
            "        //SomeModule end",
            "    ]",
            "}"
        )
    }

    @Test
    fun `should move already registered paths into the module block`() {
        val given = listOf(
            "{",
            "    \"include\": [",
            "        \"main/OtherModule/Api/ValueObjects.ts\",",
            "        \"main/SomeModule/Impl/Logic.ts\",",
            "    ]",
            "}"
        )

        val result = addSomeModuleFiles(given)

        assertThat(result).containsExactly(
            "{",
            "    \"include\": [",
            "        \"main/OtherModule/Api/ValueObjects.ts\",",
            "",
            "        //SomeModule start",
            "        \"main/SomeModule/Api/ValueObjects.ts\",",
            "",
            "        \"main/SomeModule/Impl/Logic.ts\",",
            "        \"main/SomeModule/Impl/ImplContext.ts\",",
            "        //SomeModule end",
            "    ]",
            "}"
        )
    }

    @Test
    fun `should not change a list that is already up to date`() {
        val given = addSomeModuleFiles(
            listOf(
                "{",
                "    \"files\": [",
                "        \"main/SomeModule/Impl/Logic.ts\",",
                "    ]",
                "}"
            )
        )

        val result = addSomeModuleFiles(given)

        assertThat(result).isEqualTo(given)
    }

    @Test
    fun `should keep paths that only look like the added ones`() {
        val given = listOf(
            "{",
            "    \"files\": [",
            "        \"other-main/SomeModule/Api/ValueObjects.ts\",",
            "    ]",
            "}"
        )

        val result = addModuleFilesToTsConfig(given, "SomeModule", listOf(apiPaths))

        assertThat(result).containsExactly(
            "{",
            "    \"files\": [",
            "        \"other-main/SomeModule/Api/ValueObjects.ts\",",
            "",
            "        //SomeModule start",
            "        \"main/SomeModule/Api/ValueObjects.ts\",",
            "        //SomeModule end",
            "    ]",
            "}"
        )
    }

    @Test
    fun `should do nothing when there is nothing to add or no list to add to`() {
        val list = listOf("{", "    \"files\": [", "    ]", "}")
        val withoutList = listOf("{", "    \"compilerOptions\": {}", "}")

        assertThat(addModuleFilesToTsConfig(list, "SomeModule", emptyList())).isEqualTo(list)
        assertThat(addSomeModuleFiles(withoutList)).isEqualTo(withoutList)
    }
}
