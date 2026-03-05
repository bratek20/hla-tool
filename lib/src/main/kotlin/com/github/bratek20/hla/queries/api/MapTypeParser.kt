package com.github.bratek20.hla.queries.api

/**
 * Centralized utility for parsing and handling map type definitions.
 * Encapsulates all map-related logic including regex patterns and type extraction.
 */
object MapTypeParser {
    private const val MAP_TYPE_PREFIX = "Map"

    /**
     * Regex pattern for matching map types in the format: <Key, Value> or <Key, Value>?
     */
    private val MAP_PATTERN = Regex("""<([^,]+),\s*([^>]+)>(\??)""")

    /**
     * Regex pattern for extracting key and value types from a Map type name.
     * Format: Map<KeyType,ValueType>
     */
    private val MAP_NAME_PATTERN = Regex("""Map<([^,]+),([^>]+)>""")

    data class MapTypeInfo(
        val keyType: String,
        val valueType: String,
        val isOptional: Boolean
    )

    /**
     * Parse a type string to check if it's a map type and extract its components.
     *
     * @param typeString The type string to parse (e.g., "<string, int>" or "<string, int>?")
     * @return MapTypeInfo if it's a map type, null otherwise
     */
    fun parseMapType(typeString: String): MapTypeInfo? {
        val match = MAP_PATTERN.find(typeString) ?: return null

        val keyType = match.groupValues[1].trim()
        val valueType = match.groupValues[2].trim()
        val isOptional = match.groupValues[3] == "?"

        return MapTypeInfo(keyType, valueType, isOptional)
    }

    /**
     * Generate a map type name in the standard format: Map<keyType,valueType>
     *
     * @param keyType The key type name
     * @param valueType The value type name
     * @return The formatted map type name
     */
    fun createMapTypeName(keyType: String, valueType: String): String {
        return "$MAP_TYPE_PREFIX<$keyType,$valueType>"
    }

    /**
     * Extract key and value types from a map type name.
     *
     * @param mapTypeName The map type name (e.g., "Map<string,int>")
     * @return Pair of (keyType, valueType) if valid, null otherwise
     */
    fun extractKeyValueTypes(mapTypeName: String): Pair<String, String>? {
        val match = MAP_NAME_PATTERN.find(mapTypeName) ?: return null

        val keyType = match.groupValues[1].trim()
        val valueType = match.groupValues[2].trim()

        return Pair(keyType, valueType)
    }

    /**
     * Check if a type name represents a map type.
     *
     * @param typeName The type name to check
     * @return true if it's a map type, false otherwise
     */
    fun isMapType(typeName: String): Boolean {
        return typeName.startsWith("$MAP_TYPE_PREFIX<") && typeName.endsWith(">")
    }
}
