package pl.bratek20.hla.generation.impl.core.language

interface LanguageStructure {
    fun moduleDirName(): String

    fun apiDirName(): String
    fun valueObjectsFileName(): String
    fun interfacesFileName(): String
    fun propertiesFileName(): String
    fun exceptionsFileName(): String
    fun enumsFileName(): String
    fun customTypesFileName(): String
    fun customTypesMapperFileName(): String

    fun fixturesDirName(): String
    fun buildersFileName(): String
    fun assertsFileName(): String

    fun webDirName(): String
    fun dtosFileName(): String
}