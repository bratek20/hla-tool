package com.github.bratek20.hla.generation.impl.languages.typescript

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.facade.api.ModuleLanguage

fun HlaProfile.isModernTypeScript(): Boolean {
    return getLanguage() == ModuleLanguage.TYPE_SCRIPT && getTypeScript()?.getModern() == true
}
