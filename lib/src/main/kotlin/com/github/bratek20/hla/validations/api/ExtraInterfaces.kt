package com.github.bratek20.hla.validations.api

interface TypeValidator<T> {
    fun validate(property: T, c: ValidationContext): ValidationResult
}

interface SimpleCustomTypeValidator<T, BaseType>: TypeValidator<T> {
    fun createFunction(): (value: BaseType) -> T
}

interface ComplexCustomTypeValidator<T, SerializedT>: TypeValidator<T> {
}