package com.github.bratek20.hla.writing.tests

import com.github.bratek20.hla.writing.impl.addEntryImport
import com.github.bratek20.hla.writing.impl.addModernLaunchConfig
import com.github.bratek20.hla.writing.impl.addModernTestScript
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ModernFileEditsTest {
    private val vitestConfig = "./vitest.config.mts"

    @Nested
    inner class EntryFile {
        @Test
        fun `should add the first import below the header`() {
            val given = listOf("// Entry point of the modern bundle.")

            val result = addEntryImport(given, "import \"./SomeModule/Web/PlayFabHandlers\"")

            assertThat(result).containsExactly(
                "// Entry point of the modern bundle.",
                "",
                "import \"./SomeModule/Web/PlayFabHandlers\""
            )
        }

        @Test
        fun `should keep imports sorted so generation order does not matter`() {
            val given = listOf(
                "// header",
                "",
                "import \"./OtherModule/Web/PlayFabHandlers\"",
                "import \"./SomeModule/Web/PlayFabHandlers\""
            )

            val result = addEntryImport(given, "import \"./OnlyInterfacesModule/Impl/ImplContext\"")

            assertThat(result).containsExactly(
                "// header",
                "",
                "import \"./OnlyInterfacesModule/Impl/ImplContext\"",
                "import \"./OtherModule/Web/PlayFabHandlers\"",
                "import \"./SomeModule/Web/PlayFabHandlers\""
            )
        }

        @Test
        fun `should not add the same import twice`() {
            val given = listOf(
                "// header",
                "",
                "import \"./SomeModule/Web/PlayFabHandlers\""
            )

            val result = addEntryImport(given, "import \"./SomeModule/Web/PlayFabHandlers\"")

            assertThat(result).isEqualTo(given)
        }

        @Test
        fun `should work without any header`() {
            val result = addEntryImport(emptyList(), "import \"./SomeModule/Web/PlayFabHandlers\"")

            assertThat(result).containsExactly("import \"./SomeModule/Web/PlayFabHandlers\"")
        }
    }

    @Nested
    inner class PackageJson {
        @Test
        fun `should add a vitest script to an empty scripts block`() {
            val given = listOf(
                "{",
                "  \"scripts\": {",
                "  }",
                "}"
            )

            val result = addModernTestScript(given, "SomeModule", vitestConfig)

            assertThat(result).containsExactly(
                "{",
                "  \"scripts\": {",
                "    \"test SomeModule\": \"npm run typecheck_modern_tests && vitest run --config ./vitest.config.mts SomeModule\"",
                "  }",
                "}"
            )
        }

        @Test
        fun `should add a comma to the previous script`() {
            val given = listOf(
                "{",
                "  \"scripts\": {",
                "    \"test OtherModule\": \"npm run typecheck_modern_tests && vitest run --config ./vitest.config.mts OtherModule\"",
                "  }",
                "}"
            )

            val result = addModernTestScript(given, "SomeModule", vitestConfig)

            assertThat(result).containsExactly(
                "{",
                "  \"scripts\": {",
                "    \"test OtherModule\": \"npm run typecheck_modern_tests && vitest run --config ./vitest.config.mts OtherModule\",",
                "    \"test SomeModule\": \"npm run typecheck_modern_tests && vitest run --config ./vitest.config.mts SomeModule\"",
                "  }",
                "}"
            )
        }

        @Test
        fun `should not add the same script twice`() {
            val given = listOf(
                "{",
                "  \"scripts\": {",
                "    \"test SomeModule\": \"anything\"",
                "  }",
                "}"
            )

            assertThat(addModernTestScript(given, "SomeModule", vitestConfig)).isEqualTo(given)
        }

        @Test
        fun `should not touch a file without a scripts block`() {
            val given = listOf("{", "}")

            assertThat(addModernTestScript(given, "SomeModule", vitestConfig)).isEqualTo(given)
        }
    }

    @Nested
    inner class LaunchJson {
        @Test
        fun `should add a vitest debug configuration to an empty configurations array`() {
            val given = listOf(
                "{",
                "    \"configurations\": [",
                "    ]",
                "}"
            )

            val result = addModernLaunchConfig(given, "SomeModule", vitestConfig)

            assertThat(result).containsExactly(
                "{",
                "    \"configurations\": [",
                "        {",
                "            \"type\": \"node\",",
                "            \"request\": \"launch\",",
                "            \"name\": \"Debug Modern Tests - SomeModule\",",
                "            \"program\": \"\${workspaceFolder}/node_modules/vitest/vitest.mjs\",",
                "            \"args\": [",
                "                \"run\",",
                "                \"--config\",",
                "                \"./vitest.config.mts\",",
                "                \"--no-file-parallelism\",",
                "                \"--testTimeout=600000\",",
                "                \"SomeModule\"",
                "            ],",
                "            \"cwd\": \"\${workspaceFolder}\",",
                "            \"console\": \"integratedTerminal\",",
                "            \"autoAttachChildProcesses\": true,",
                "            \"smartStep\": true,",
                "            \"skipFiles\": [",
                "                \"<node_internals>/**\",",
                "                \"**/node_modules/**\"",
                "            ]",
                "        }",
                "    ]",
                "}"
            )
        }

        @Test
        fun `should not be confused by braces inside workspaceFolder values`() {
            val given = listOf(
                "{",
                "    \"configurations\": [",
                "        {",
                "            \"name\": \"Debug Modern Tests - OtherModule\",",
                "            \"cwd\": \"\${workspaceFolder}\"",
                "        }",
                "    ]",
                "}"
            )

            val result = addModernLaunchConfig(given, "SomeModule", vitestConfig)

            assertThat(result[5]).isEqualTo("        },")
            assertThat(result[6]).isEqualTo("        {")
            assertThat(result).contains("            \"name\": \"Debug Modern Tests - SomeModule\",")
            assertThat(result.last()).isEqualTo("}")
            assertThat(result[result.size - 2]).isEqualTo("    ]")
        }

        @Test
        fun `should not add the same configuration twice`() {
            val given = addModernLaunchConfig(
                listOf("{", "    \"configurations\": [", "    ]", "}"),
                "SomeModule",
                vitestConfig
            )

            assertThat(addModernLaunchConfig(given, "SomeModule", vitestConfig)).isEqualTo(given)
        }
    }
}
