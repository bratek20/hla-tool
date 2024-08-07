// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.facade.fixtures

import com.github.bratek20.utils.directory.api.*
import com.github.bratek20.utils.directory.fixtures.*

import com.github.bratek20.hla.facade.api.*

fun moduleName(value: String = "someValue"): ModuleName {
    return ModuleName(value)
}

fun profileName(value: String = "someValue"): ProfileName {
    return ProfileName(value)
}

data class ModuleOperationArgsDef(
    var hlaFolderPath: String = "someValue",
    var profileName: String = "someValue",
    var moduleName: String = "someValue",
)
fun moduleOperationArgs(init: ModuleOperationArgsDef.() -> Unit = {}): ModuleOperationArgs {
    val def = ModuleOperationArgsDef().apply(init)
    return ModuleOperationArgs.create(
        hlaFolderPath = pathCreate(def.hlaFolderPath),
        profileName = ProfileName(def.profileName),
        moduleName = ModuleName(def.moduleName),
    )
}

data class AllModulesOperationArgsDef(
    var hlaFolderPath: String = "someValue",
    var profileName: String = "someValue",
)
fun allModulesOperationArgs(init: AllModulesOperationArgsDef.() -> Unit = {}): AllModulesOperationArgs {
    val def = AllModulesOperationArgsDef().apply(init)
    return AllModulesOperationArgs.create(
        hlaFolderPath = pathCreate(def.hlaFolderPath),
        profileName = ProfileName(def.profileName),
    )
}

data class TypeScriptConfigDef(
    var mainTsconfigPath: String = "someValue",
    var testTsconfigPath: String = "someValue",
    var launchJsonPath: String = "someValue",
    var packageJsonPath: String = "someValue",
)
fun typeScriptConfig(init: TypeScriptConfigDef.() -> Unit = {}): TypeScriptConfig {
    val def = TypeScriptConfigDef().apply(init)
    return TypeScriptConfig.create(
        mainTsconfigPath = pathCreate(def.mainTsconfigPath),
        testTsconfigPath = pathCreate(def.testTsconfigPath),
        launchJsonPath = pathCreate(def.launchJsonPath),
        packageJsonPath = pathCreate(def.packageJsonPath),
    )
}

data class HlaSrcPathsDef(
    var main: String = "someValue",
    var test: String = "someValue",
    var fixtures: String = "someValue",
)
fun hlaSrcPaths(init: HlaSrcPathsDef.() -> Unit = {}): HlaSrcPaths {
    val def = HlaSrcPathsDef().apply(init)
    return HlaSrcPaths.create(
        main = pathCreate(def.main),
        test = pathCreate(def.test),
        fixtures = pathCreate(def.fixtures),
    )
}

data class HlaPathsDef(
    var project: String = "someValue",
    var src: (HlaSrcPathsDef.() -> Unit) = {},
)
fun hlaPaths(init: HlaPathsDef.() -> Unit = {}): HlaPaths {
    val def = HlaPathsDef().apply(init)
    return HlaPaths.create(
        project = pathCreate(def.project),
        src = hlaSrcPaths(def.src),
    )
}

data class HlaProfileImportDef(
    var hlaFolderPath: String = "someValue",
    var profileName: String = "someValue",
)
fun hlaProfileImport(init: HlaProfileImportDef.() -> Unit = {}): HlaProfileImport {
    val def = HlaProfileImportDef().apply(init)
    return HlaProfileImport.create(
        hlaFolderPath = pathCreate(def.hlaFolderPath),
        profileName = ProfileName(def.profileName),
    )
}

data class HlaProfileDef(
    var name: String = "someValue",
    var language: String = ModuleLanguage.KOTLIN.name,
    var paths: (HlaPathsDef.() -> Unit) = {},
    var typeScript: (TypeScriptConfigDef.() -> Unit)? = null,
    var onlyPatterns: List<String> = emptyList(),
    var imports: List<(HlaProfileImportDef.() -> Unit)> = emptyList(),
)
fun hlaProfile(init: HlaProfileDef.() -> Unit = {}): HlaProfile {
    val def = HlaProfileDef().apply(init)
    return HlaProfile.create(
        name = ProfileName(def.name),
        language = ModuleLanguage.valueOf(def.language),
        paths = hlaPaths(def.paths),
        typeScript = def.typeScript?.let { it -> typeScriptConfig(it) },
        onlyPatterns = def.onlyPatterns,
        imports = def.imports.map { it -> hlaProfileImport(it) },
    )
}