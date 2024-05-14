package pl.bratek20.hla.facade.fixtures

import pl.bratek20.hla.directory.api.*
import pl.bratek20.hla.directory.fixtures.*

import pl.bratek20.hla.facade.api.*

data class GenerateModuleArgsDef(
    var moduleName: String = "someValue",
    var language: ModuleLanguage = ModuleLanguage.KOTLIN,
    var hlaFolderPath: String = "someValue",
    var projectPath: String = "someValue",
)
fun generateModuleArgs(init: GenerateModuleArgsDef.() -> Unit = {}): GenerateModuleArgs {
    val def = GenerateModuleArgsDef().apply(init)
    return GenerateModuleArgs(
        moduleName = ModuleName(def.moduleName),
        language = def.language,
        hlaFolderPath = Path(def.hlaFolderPath),
        projectPath = Path(def.projectPath),
    )
}

data class KotlinPropertiesDef(
    var rootPackage: String = "someValue",
)
fun kotlinProperties(init: KotlinPropertiesDef.() -> Unit = {}): KotlinProperties {
    val def = KotlinPropertiesDef().apply(init)
    return KotlinProperties(
        rootPackage = def.rootPackage,
    )
}

data class TypeScriptPropertiesDef(
    var srcPath: String = "someValue",
    var testPath: String = "someValue",
)
fun typeScriptProperties(init: TypeScriptPropertiesDef.() -> Unit = {}): TypeScriptProperties {
    val def = TypeScriptPropertiesDef().apply(init)
    return TypeScriptProperties(
        srcPath = def.srcPath,
        testPath = def.testPath,
    )
}

data class HlaPropertiesDef(
    var generateWeb: Boolean = false,
    var kotlin: (KotlinPropertiesDef.() -> Unit) = {},
    var typeScript: (TypeScriptPropertiesDef.() -> Unit) = {},
)
fun hlaProperties(init: HlaPropertiesDef.() -> Unit = {}): HlaProperties {
    val def = HlaPropertiesDef().apply(init)
    return HlaProperties(
        generateWeb = def.generateWeb,
        kotlin = kotlinProperties(def.kotlin),
        typeScript = typeScriptProperties(def.typeScript),
    )
}