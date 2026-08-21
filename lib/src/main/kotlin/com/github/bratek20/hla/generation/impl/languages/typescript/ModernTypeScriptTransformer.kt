package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.asHla
import com.github.bratek20.hla.hlatypesworld.api.isHla
import com.github.bratek20.hla.queries.api.BaseModuleGroupQueries
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.hla.typesworld.api.findByName
import com.github.bratek20.utils.camelToPascalCase
import com.github.bratek20.utils.directory.api.FileContent

private val IDENTIFIER = Regex("[A-Za-z_$][\\w$]*")
private val NAMED_IMPORT = Regex("^import \\{([^}]*)\\}")
private val NAMESPACE_IMPORT = Regex("^import \\* as ([A-Za-z_$][\\w$]*)")

// Fixture entry points share camelCase names across Builders and Asserts, so they are
// pulled in as namespace objects the way the reference project does it, keeping the
// call sites readable: Builder.someClass(...), OtherModuleAssert.otherClass(...).
private val NAMESPACE_QUALIFIERS = mapOf(
    "Builder" to (SubmoduleName.Fixtures to PatternName.Builders),
    "Assert" to (SubmoduleName.Fixtures to PatternName.Asserts),
    "CustomTypesMapper" to (SubmoduleName.Api to PatternName.CustomTypesMapper),
)

// Qualifiers that name exactly one file
private val FILE_QUALIFIERS = mapOf(
    "Mocks" to (SubmoduleName.Fixtures to PatternName.Mocks),
    "Menu" to (SubmoduleName.Menu to PatternName.Menu),
)

// Qualifiers that name a whole submodule, so the member decides the file. `Impl` in
// particular spans Logic, ImplContext, Track and DataClasses - it must not be assumed
// to be Logic, or tracking dimensions get imported from the wrong file.
private val SUBMODULE_QUALIFIERS = mapOf(
    "Impl" to SubmoduleName.Impl,
    "Web" to SubmoduleName.Web,
)

class ModernTypeScriptTransformer(
    profile: HlaProfile,
    queries: BaseModuleGroupQueries,
    private val typesWorldApi: TypesWorldApi,
) {
    private val paths = ModernTypeScriptPaths(profile)

    // Only modules that are themselves generated as ES modules may be imported from.
    // Everything else - a legacy module sitting next to a migrated one in the same group,
    // or any module pulled in through `imports`, whose group has its own profile - stays
    // reachable through its `Module.X.y` global and must never get an ES import.
    private val modernModules = queries.group.getModules().filter { it.isMarkedModern() }
    private val exports = ModernTypeScriptExports(modernModules)
    private val moduleNames = modernModules.map { it.getName() }.toSet()
    private val qualifiedPatterns = moduleNames.associateWith {
        Regex("\\b${Regex.escape(it.value)}\\.([A-Za-z_$][\\w$]*)(\\.([A-Za-z_$][\\w$]*))?")
    }

    fun transform(content: FileContent, self: ModernTypeScriptFile): FileContent {
        val unwrapped = ModernTypeScriptSource.unwrapNamespaces(content.lines)
        val exported = ModernTypeScriptSource.addMissingExports(unwrapped)

        // Templates may already write their own imports, e.g. vitest in ImplTest
        val templateImports = exported.filter { it.startsWith("import ") }
        var lines = exported.filterNot { it.startsWith("import ") }

        val declared = ModernTypeScriptSource.declaredNames(lines) + importedNames(templateImports)
        val imports = Imports(self, declared)

        lines = lines.map { line ->
            if (isCommentLine(line)) line
            else mapCodeSegments(line) { rewriteQualifiedReferences(it, self, imports) }
        }
        lines = lines.map { line ->
            if (isCommentLine(line)) line
            else mapCodeSegments(line) { rewriteBareReferences(it, self, imports) }
        }

        val importLines = templateImports + imports.render(paths)
        val body = lines.dropWhile { it.isBlank() }
        val result = if (importLines.isEmpty()) body else importLines + "" + body

        return FileContent(if (result.lastOrNull() == "") result else result + "")
    }

    // Doc comments carry @throws references and prose apostrophes, neither of which
    // should drive imports or be mistaken for a string literal.
    private fun isCommentLine(line: String): Boolean {
        val trimmed = line.trimStart()
        return trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*")
    }

    private fun importedNames(importLines: List<String>): Set<String> {
        return importLines.flatMap { line ->
            NAMESPACE_IMPORT.find(line)?.let { return@flatMap listOf(it.groupValues[1]) }
            val names = NAMED_IMPORT.find(line)?.groupValues?.get(1) ?: return@flatMap emptyList<String>()
            names.split(",").map { it.substringAfter(" as ").trim() }.filter { it.isNotEmpty() }
        }.toSet()
    }

    private fun rewriteQualifiedReferences(code: String, self: ModernTypeScriptFile, imports: Imports): String {
        var result = code
        qualifiedPatterns.forEach { (module, pattern) ->
            result = pattern.replace(result) { match ->
                resolveQualified(module, match.groupValues[1], match.groupValues[3], self, imports)
                    ?: match.value
            }
        }
        return result
    }

    private fun resolveQualified(
        module: ModuleName,
        qualifier: String,
        member: String,
        self: ModernTypeScriptFile,
        imports: Imports,
    ): String? {
        NAMESPACE_QUALIFIERS[qualifier]?.let { (submodule, pattern) ->
            val file = ModernTypeScriptFile(module, submodule, pattern)
            val alias = if (module == self.module) qualifier else "${module.value}$qualifier"
            imports.addNamespace(file, alias)
            return if (member.isEmpty()) alias else "$alias.$member"
        }

        FILE_QUALIFIERS[qualifier]?.let { (submodule, pattern) ->
            if (member.isEmpty()) return null
            return imports.addNamed(ModernTypeScriptFile(module, submodule, pattern), member, module)
        }

        if (qualifier == ModernTypeScriptExports.API_OBJECT) {
            val file = ModernTypeScriptFile(module, SubmoduleName.Impl, PatternName.ImplContext)
            val reference = imports.addNamed(file, ModernTypeScriptExports.API_OBJECT, module)
            return if (member.isEmpty()) reference else "$reference.$member"
        }

        SUBMODULE_QUALIFIERS[qualifier]?.let { submodule ->
            if (member.isEmpty()) return null
            val file = exports.findInSubmodule(member, module, submodule) ?: return null
            return imports.addNamed(file, member, module)
        }

        // Bare `Module.symbol`, as emitted for Diffs, keys and TestBase members.
        val file = exports.findInModule(qualifier, module) ?: return null
        val reference = imports.addNamed(file, qualifier, module)
        return if (member.isEmpty()) reference else "$reference.$member"
    }

    private fun rewriteBareReferences(code: String, self: ModernTypeScriptFile, imports: Imports): String {
        return IDENTIFIER.replace(code) { match ->
            if (isMemberAccess(code, match.range.first)) {
                match.value
            } else {
                val file = resolveBare(match.value, self)
                if (file == null) match.value
                else imports.addNamed(file, match.value, file.module)
            }
        }
    }

    private fun isMemberAccess(code: String, index: Int): Boolean {
        val before = index - 1
        return before >= 0 && (code[before] == '.' || code[before] == '?')
    }

    private fun resolveBare(name: String, self: ModernTypeScriptFile): ModernTypeScriptFile? {
        val file = fromTypesWorld(name) ?: exports.find(name, self.module)
        return file?.takeIf { it != self }
    }

    // TypesWorld is the source of truth for api types. It also holds primitives and b20
    // frontend types, whose paths parse as module paths, hence the known module check.
    private fun fromTypesWorld(name: String): ModernTypeScriptFile? {
        val path = typesWorldApi.findByName(WorldTypeName(name))?.getPath() ?: return null
        if (!path.isHla()) {
            return null
        }
        val hlaPath = path.asHla()
        if (!moduleNames.contains(hlaPath.getModuleName())) {
            return null
        }
        return runCatching { hlaPath.asModernTypeScriptFile() }.getOrNull()
    }
}

private class Imports(
    private val self: ModernTypeScriptFile,
    private val declared: Set<String>,
) {
    private val namespaces = mutableMapOf<ModernTypeScriptFile, String>()
    private val named = mutableMapOf<ModernTypeScriptFile, MutableMap<String, String>>()

    fun addNamespace(file: ModernTypeScriptFile, alias: String) {
        if (file == self) return
        namespaces[file] = alias
    }

    // Returns the name to use at the call site, aliasing when the plain name is already
    // taken by a declaration in this file or by an import from a different file.
    fun addNamed(file: ModernTypeScriptFile, name: String, module: ModuleName): String {
        if (file == self) return name

        named[file]?.get(name)?.let { return it }

        val taken = declared.contains(name) || named.any { (_, names) -> names.containsValue(name) }
        val alias = if (taken) "${module.value}${camelToPascalCase(name)}" else name

        named.computeIfAbsent(file) { mutableMapOf() }[name] = alias
        return alias
    }

    fun render(paths: ModernTypeScriptPaths): List<String> {
        val lines = mutableListOf<Pair<String, String>>()

        namespaces.forEach { (file, alias) ->
            val path = paths.calculateImportPath(self, file)
            lines.add(path to "import * as $alias from \"$path\"")
        }

        named.forEach { (file, names) ->
            if (names.isEmpty()) return@forEach
            val path = paths.calculateImportPath(self, file)
            val rendered = names.entries
                .sortedBy { it.key }
                .joinToString(", ") { (name, alias) -> if (name == alias) name else "$name as $alias" }
            lines.add(path to "import { $rendered } from \"$path\"")
        }

        return lines.sortedBy { it.first }.map { it.second }
    }
}

// Applies the transform only outside string literals and line comments, so that
// handler name literals like "SomeModule.someHandler" survive untouched.
private fun mapCodeSegments(line: String, transform: (String) -> String): String {
    val result = StringBuilder()
    val code = StringBuilder()
    var index = 0

    fun flushCode() {
        if (code.isNotEmpty()) {
            result.append(transform(code.toString()))
            code.clear()
        }
    }

    while (index < line.length) {
        val char = line[index]
        when {
            char == '/' && index + 1 < line.length && line[index + 1] == '/' -> {
                flushCode()
                result.append(line.substring(index))
                return result.toString()
            }
            char == '"' || char == '\'' || char == '`' -> {
                flushCode()
                val end = endOfStringLiteral(line, index, char)
                result.append(line, index, end)
                index = end
            }
            else -> {
                code.append(char)
                index++
            }
        }
    }
    flushCode()
    return result.toString()
}

private fun endOfStringLiteral(line: String, startIndex: Int, quote: Char): Int {
    var index = startIndex + 1
    while (index < line.length) {
        when (line[index]) {
            '\\' -> index += 2
            quote -> return index + 1
            else -> index++
        }
    }
    return line.length
}
