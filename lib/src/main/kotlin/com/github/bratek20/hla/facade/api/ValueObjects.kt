// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.facade.api

import com.github.bratek20.hla.directory.api.*

data class ModuleName(
    val value: String
)

data class ProfileName(
    val value: String
)

data class TypeScriptConfig(
    private val mainTsconfigPath: String,
    private val testTsconfigPath: String,
    private val launchJsonPath: String,
    private val packageJsonPath: String,
) {
    fun getMainTsconfigPath(): Path {
        return pathCreate(this.mainTsconfigPath)
    }

    fun getTestTsconfigPath(): Path {
        return pathCreate(this.testTsconfigPath)
    }

    fun getLaunchJsonPath(): Path {
        return pathCreate(this.launchJsonPath)
    }

    fun getPackageJsonPath(): Path {
        return pathCreate(this.packageJsonPath)
    }

    companion object {
        fun create(
            mainTsconfigPath: Path,
            testTsconfigPath: Path,
            launchJsonPath: Path,
            packageJsonPath: Path,
        ): TypeScriptConfig {
            return TypeScriptConfig(
                mainTsconfigPath = pathGetValue(mainTsconfigPath),
                testTsconfigPath = pathGetValue(testTsconfigPath),
                launchJsonPath = pathGetValue(launchJsonPath),
                packageJsonPath = pathGetValue(packageJsonPath),
            )
        }
    }
}

data class HlaSrcPaths(
    private val main: String,
    private val test: String,
    private val fixtures: String,
) {
    fun getMain(): Path {
        return pathCreate(this.main)
    }

    fun getTest(): Path {
        return pathCreate(this.test)
    }

    fun getFixtures(): Path {
        return pathCreate(this.fixtures)
    }

    companion object {
        fun create(
            main: Path,
            test: Path,
            fixtures: Path,
        ): HlaSrcPaths {
            return HlaSrcPaths(
                main = pathGetValue(main),
                test = pathGetValue(test),
                fixtures = pathGetValue(fixtures),
            )
        }
    }
}

data class HlaPaths(
    private val project: String,
    private val src: HlaSrcPaths,
) {
    fun getProject(): Path {
        return pathCreate(this.project)
    }

    fun getSrc(): HlaSrcPaths {
        return this.src
    }

    companion object {
        fun create(
            project: Path,
            src: HlaSrcPaths,
        ): HlaPaths {
            return HlaPaths(
                project = pathGetValue(project),
                src = src,
            )
        }
    }
}

data class HlaProfileImport(
    private val hlaFolderPath: String,
    private val profileName: String,
) {
    fun getHlaFolderPath(): Path {
        return pathCreate(this.hlaFolderPath)
    }

    fun getProfileName(): ProfileName {
        return ProfileName(this.profileName)
    }

    companion object {
        fun create(
            hlaFolderPath: Path,
            profileName: ProfileName,
        ): HlaProfileImport {
            return HlaProfileImport(
                hlaFolderPath = pathGetValue(hlaFolderPath),
                profileName = profileName.value,
            )
        }
    }
}

data class HlaProfile(
    private val name: String,
    private val language: String,
    private val paths: HlaPaths,
    private val onlyPatterns: List<String>,
    private val typeScript: TypeScriptConfig?,
    private val imports: List<HlaProfileImport> = emptyList(),
) {
    fun getName(): ProfileName {
        return ProfileName(this.name)
    }

    fun getLanguage(): ModuleLanguage {
        return ModuleLanguage.valueOf(this.language)
    }

    fun getPaths(): HlaPaths {
        return this.paths
    }

    fun getOnlyPatterns(): List<String> {
        return this.onlyPatterns
    }

    fun getTypeScript(): TypeScriptConfig? {
        return this.typeScript
    }

    fun getImports(): List<HlaProfileImport> {
        return this.imports
    }

    companion object {
        fun create(
            name: ProfileName,
            language: ModuleLanguage,
            paths: HlaPaths,
            onlyPatterns: List<String>,
            typeScript: TypeScriptConfig?,
            imports: List<HlaProfileImport> = emptyList(),
        ): HlaProfile {
            return HlaProfile(
                name = name.value,
                language = language.name,
                paths = paths,
                onlyPatterns = onlyPatterns,
                typeScript = typeScript,
                imports = imports,
            )
        }
    }
}

data class ModuleOperationArgs(
    private val hlaFolderPath: String,
    private val profileName: String,
    private val moduleName: String,
) {
    fun getHlaFolderPath(): Path {
        return pathCreate(this.hlaFolderPath)
    }

    fun getProfileName(): ProfileName {
        return ProfileName(this.profileName)
    }

    fun getModuleName(): ModuleName {
        return ModuleName(this.moduleName)
    }

    companion object {
        fun create(
            hlaFolderPath: Path,
            profileName: ProfileName,
            moduleName: ModuleName,
        ): ModuleOperationArgs {
            return ModuleOperationArgs(
                hlaFolderPath = pathGetValue(hlaFolderPath),
                profileName = profileName.value,
                moduleName = moduleName.value,
            )
        }
    }
}

data class AllModulesOperationArgs(
    private val hlaFolderPath: String,
    private val profileName: String,
) {
    fun getHlaFolderPath(): Path {
        return pathCreate(this.hlaFolderPath)
    }

    fun getProfileName(): ProfileName {
        return ProfileName(this.profileName)
    }

    companion object {
        fun create(
            hlaFolderPath: Path,
            profileName: ProfileName,
        ): AllModulesOperationArgs {
            return AllModulesOperationArgs(
                hlaFolderPath = pathGetValue(hlaFolderPath),
                profileName = profileName.value,
            )
        }
    }
}