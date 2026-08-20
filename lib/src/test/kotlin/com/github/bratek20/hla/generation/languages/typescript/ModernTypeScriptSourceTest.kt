package com.github.bratek20.hla.generation.languages.typescript

import com.github.bratek20.hla.generation.impl.languages.typescript.ModernTypeScriptSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ModernTypeScriptSourceTest {
    @Test
    fun `should unwrap namespace and dedent its body`() {
        val given = listOf(
            "namespace SomeModule.Api {",
            "    export function someMethod(): void {",
            "        doSomething()",
            "    }",
            "}"
        )

        assertThat(ModernTypeScriptSource.unwrapNamespaces(given)).containsExactly(
            "export function someMethod(): void {",
            "    doSomething()",
            "}"
        )
    }

    @Test
    fun `should unwrap nested namespaces`() {
        val given = listOf(
            "class SomeMock {",
            "}",
            "namespace SomeModule {",
            "    namespace Mocks {",
            "        export function setupSome(): void {",
            "        }",
            "    }",
            "}"
        )

        assertThat(ModernTypeScriptSource.unwrapNamespaces(given)).containsExactly(
            "class SomeMock {",
            "}",
            "export function setupSome(): void {",
            "}"
        )
    }

    @Test
    fun `should not be confused by braces inside strings`() {
        val given = listOf(
            "namespace SomeModule {",
            "    export function diffSome(path: string): string {",
            "        return `\${path}items[\${idx}] {\"",
            "    }",
            "}"
        )

        assertThat(ModernTypeScriptSource.unwrapNamespaces(given)).containsExactly(
            "export function diffSome(path: string): string {",
            "    return `\${path}items[\${idx}] {\"",
            "}"
        )
    }

    @Test
    fun `should add export only to top level declarations that lack it`() {
        val given = listOf(
            "class SomeId {",
            "    private static cache = new Map<string, SomeId>()",
            "    getValue(): string {",
            "        const instance = new SomeId()",
            "        return this.value",
            "    }",
            "}",
            "export interface SomeInterface {",
            "}",
            "const SOME_KEY_PROPERTY_KEY = new ObjectPropertyKey()"
        )

        assertThat(ModernTypeScriptSource.addMissingExports(given)).containsExactly(
            "export class SomeId {",
            "    private static cache = new Map<string, SomeId>()",
            "    getValue(): string {",
            "        const instance = new SomeId()",
            "        return this.value",
            "    }",
            "}",
            "export interface SomeInterface {",
            "}",
            "export const SOME_KEY_PROPERTY_KEY = new ObjectPropertyKey()"
        )
    }

    @Test
    fun `should collect declared names`() {
        val given = listOf(
            "export class SomeId {",
            "    getValue(): string {",
            "        const instance = new SomeId()",
            "    }",
            "}",
            "export interface SomeClassDef {",
            "}",
            "export function someClass(): SomeClass {",
            "}"
        )

        assertThat(ModernTypeScriptSource.declaredNames(given))
            .containsExactlyInAnyOrder("SomeId", "SomeClassDef", "someClass")
    }
}
