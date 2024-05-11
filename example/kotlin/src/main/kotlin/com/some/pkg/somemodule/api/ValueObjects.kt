package com.some.pkg.somemodule.api

import com.some.pkg.othermodule.api.OtherClass
import com.some.pkg.othermodule.api.OtherId

data class SomeId(
    val value: String
)

data class SomeClass(
    val id: SomeId,
    val amount: Int,
)

data class SomeClass2(
    val id: SomeId,
    val enabled: Boolean,
    val names: List<String>,
    val ids: List<SomeId>,
)

data class SomeClass3(
    val class2Object: SomeClass2,
    val class2List: List<SomeClass2>,
    val someEnum: SomeEnum,
)

data class SomeClass4(
    val otherId: OtherId,
    val otherClass: OtherClass,
    val otherIdList: List<OtherId>,
    val otherClassList: List<OtherClass>,
)