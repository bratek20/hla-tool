// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.somemodule.api

import com.some.pkg.othermodule.api.*
import com.some.pkg.typesmodule.api.*

data class SomeId(
    val value: String
)

data class SomeId2(
    val value: Int
)

data class SomeProperty(
    private val other: OtherProperty,
    private val id2: Int?,
    private val range: SerializedDateRange?,
    private val doubleExample: Double,
    private val longExample: Long,
) {
    fun getOther(): OtherProperty {
        return this.other
    }

    fun getId2(): SomeId2? {
        return this.id2?.let { it -> SomeId2(it) }
    }

    fun getRange(): DateRange? {
        return this.range?.let { it -> it.toCustomType() }
    }

    fun getDoubleExample(): Double {
        return this.doubleExample
    }

    fun getLongExample(): Long {
        return this.longExample
    }

    companion object {
        fun create(
            other: OtherProperty,
            id2: SomeId2?,
            range: DateRange?,
            doubleExample: Double,
            longExample: Long,
        ): SomeProperty {
            return SomeProperty(
                other = other,
                id2 = id2?.let { it -> it.value },
                range = range?.let { it -> SerializedDateRange.fromCustomType(it) },
                doubleExample = doubleExample,
                longExample = longExample,
            )
        }
    }
}

data class SomeProperty2(
    @JvmField val value: String,
    private val custom: Any,
    private val someEnum: String,
    private val customOpt: Any?,
) {
    fun getValue(): String {
        return this.value
    }

    fun getCustom(): Any {
        return this.custom
    }

    fun getSomeEnum(): SomeEnum {
        return SomeEnum.valueOf(this.someEnum)
    }

    fun getCustomOpt(): Any? {
        return this.customOpt
    }

    companion object {
        fun create(
            value: String,
            custom: Any,
            someEnum: SomeEnum,
            customOpt: Any?,
        ): SomeProperty2 {
            return SomeProperty2(
                value = value,
                custom = custom,
                someEnum = someEnum.name,
                customOpt = customOpt,
            )
        }
    }
}

data class SomeClass(
    private val id: String,
    private val amount: Int,
) {
    fun getId(): SomeId {
        return SomeId(this.id)
    }

    fun getAmount(): Int {
        return this.amount
    }

    companion object {
        fun create(
            id: SomeId,
            amount: Int,
        ): SomeClass {
            return SomeClass(
                id = id.value,
                amount = amount,
            )
        }
    }
}

data class SomeClass2(
    private val id: String,
    private val names: List<String>,
    private val ids: List<SomeId>,
    private val enabled: Boolean,
) {
    fun getId(): SomeId {
        return SomeId(this.id)
    }

    fun getNames(): List<String> {
        return this.names
    }

    fun getIds(): List<SomeId> {
        return this.ids
    }

    fun getEnabled(): Boolean {
        return this.enabled
    }

    companion object {
        fun create(
            id: SomeId,
            names: List<String>,
            ids: List<SomeId>,
            enabled: Boolean,
        ): SomeClass2 {
            return SomeClass2(
                id = id.value,
                names = names,
                ids = ids,
                enabled = enabled,
            )
        }
    }
}

data class SomeClass3(
    private val class2Object: SomeClass2,
    private val someEnum: String,
    private val class2List: List<SomeClass2>,
) {
    fun getClass2Object(): SomeClass2 {
        return this.class2Object
    }

    fun getSomeEnum(): SomeEnum {
        return SomeEnum.valueOf(this.someEnum)
    }

    fun getClass2List(): List<SomeClass2> {
        return this.class2List
    }

    companion object {
        fun create(
            class2Object: SomeClass2,
            someEnum: SomeEnum,
            class2List: List<SomeClass2>,
        ): SomeClass3 {
            return SomeClass3(
                class2Object = class2Object,
                someEnum = someEnum.name,
                class2List = class2List,
            )
        }
    }
}

data class SomeClass4(
    private val otherId: Int,
    private val otherClass: OtherClass,
    private val otherIdList: List<OtherId>,
    private val otherClassList: List<OtherClass>,
) {
    fun getOtherId(): OtherId {
        return OtherId(this.otherId)
    }

    fun getOtherClass(): OtherClass {
        return this.otherClass
    }

    fun getOtherIdList(): List<OtherId> {
        return this.otherIdList
    }

    fun getOtherClassList(): List<OtherClass> {
        return this.otherClassList
    }

    companion object {
        fun create(
            otherId: OtherId,
            otherClass: OtherClass,
            otherIdList: List<OtherId>,
            otherClassList: List<OtherClass>,
        ): SomeClass4 {
            return SomeClass4(
                otherId = otherId.value,
                otherClass = otherClass,
                otherIdList = otherIdList,
                otherClassList = otherClassList,
            )
        }
    }
}

data class SomeClass5(
    private val date: String,
    private val dateRange: SerializedDateRange,
    private val dateRangeWrapper: SerializedDateRangeWrapper,
    private val someProperty: SomeProperty,
    private val otherProperty: OtherProperty,
) {
    fun getDate(): Date {
        return dateCreate(this.date)
    }

    fun getDateRange(): DateRange {
        return this.dateRange.toCustomType()
    }

    fun getDateRangeWrapper(): DateRangeWrapper {
        return this.dateRangeWrapper.toCustomType()
    }

    fun getSomeProperty(): SomeProperty {
        return this.someProperty
    }

    fun getOtherProperty(): OtherProperty {
        return this.otherProperty
    }

    companion object {
        fun create(
            date: Date,
            dateRange: DateRange,
            dateRangeWrapper: DateRangeWrapper,
            someProperty: SomeProperty,
            otherProperty: OtherProperty,
        ): SomeClass5 {
            return SomeClass5(
                date = dateGetValue(date),
                dateRange = SerializedDateRange.fromCustomType(dateRange),
                dateRangeWrapper = SerializedDateRangeWrapper.fromCustomType(dateRangeWrapper),
                someProperty = someProperty,
                otherProperty = otherProperty,
            )
        }
    }
}

data class SomeClass6(
    private val someClassOpt: SomeClass?,
    private val optString: String?,
    private val sameClassList: List<SomeClass6>,
) {
    fun getSomeClassOpt(): SomeClass? {
        return this.someClassOpt
    }

    fun getOptString(): String? {
        return this.optString
    }

    fun getSameClassList(): List<SomeClass6> {
        return this.sameClassList
    }

    companion object {
        fun create(
            someClassOpt: SomeClass?,
            optString: String?,
            sameClassList: List<SomeClass6>,
        ): SomeClass6 {
            return SomeClass6(
                someClassOpt = someClassOpt,
                optString = optString,
                sameClassList = sameClassList,
            )
        }
    }
}