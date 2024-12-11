package com.github.bratek20.hla.validations.api

interface TypeValidator<T> {
    fun validate(property: T): ValidationResult
}