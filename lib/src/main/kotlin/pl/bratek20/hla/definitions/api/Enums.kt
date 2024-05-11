package pl.bratek20.hla.definitions.api

enum class BaseType {
    STRING,
    INT,
    BOOL,
    VOID;

    companion object {
        fun of(value: String): BaseType {
            return BaseType.valueOf(value.uppercase())
        }

        fun isBaseType(value: String): Boolean {
            return entries.any { it.name == value.uppercase() }
        }
    }
}

enum class TypeWrapper {
    LIST,
    OPTIONAL
}