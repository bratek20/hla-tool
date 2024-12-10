package com.github.bratek20.hla.validations.api

interface TypeValidator<T> {
    fun getType(): Class<T>
    fun validate(property: T): ValidationResult
}