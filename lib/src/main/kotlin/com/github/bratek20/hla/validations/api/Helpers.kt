package com.github.bratek20.hla.validations.api

fun ValidationResult.Companion.ok(): ValidationResult {
    return ValidationResult(true, emptyList())
}

fun ValidationResult.Companion.createFor(vararg errors: String): ValidationResult {
    return createFor(errors.toList())
}

fun ValidationResult.Companion.createFor(errors: List<String>): ValidationResult {
    return ValidationResult(errors.isEmpty(), errors)
}

fun ValidationResult.merge(other: ValidationResult): ValidationResult {
    return ValidationResult(
        ok = this.getOk() && other.getOk(),
        errors = this.getErrors() + other.getErrors()
    )
}