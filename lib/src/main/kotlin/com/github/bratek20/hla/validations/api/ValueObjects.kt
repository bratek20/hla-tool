// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.validations.api

import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.utils.directory.api.*

data class ValidationResult(
    private val ok: Boolean,
    private val errors: List<String>,
) {
    fun getOk(): Boolean {
        return this.ok
    }

    fun getErrors(): List<String> {
        return this.errors
    }

    companion object {
        fun create(
            ok: Boolean,
            errors: List<String>,
        ): ValidationResult {
            return ValidationResult(
                ok = ok,
                errors = errors,
            )
        }
    }
}