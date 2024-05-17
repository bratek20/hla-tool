package com.some.pkg.somemodule.web

import com.some.pkg.othermodule.api.*
import com.some.pkg.othermodule.web.*
import com.some.pkg.typesmodule.api.*
import com.some.pkg.typesmodule.web.*

import com.some.pkg.somemodule.api.*

data class DateRangeWrapperDto(
    val range: DateRangeDto,
) {
    fun toApi(): DateRangeWrapper {
        return dateRangeWrapperCreate(
            range = range.toApi(),
        )
    }

    companion object {
        fun fromApi(api: DateRangeWrapper): DateRangeWrapperDto {
            return DateRangeWrapperDto(
                range = DateRangeDto.fromApi(dateRangeWrapperGetRange(api)),
            )
        }
    }
}

data class SomeClassDto(
    val id: String,
    val amount: Int,
) {
    fun toApi(): SomeClass {
        return SomeClass(
            id = SomeId(id),
            amount = amount,
        )
    }

    companion object {
        fun fromApi(api: SomeClass): SomeClassDto {
            return SomeClassDto(
                id = api.id.value,
                amount = api.amount,
            )
        }
    }
}

data class SomeClass2Dto(
    val id: String,
    val enabled: Boolean,
    val names: List<String>,
    val ids: List<String>,
) {
    fun toApi(): SomeClass2 {
        return SomeClass2(
            id = SomeId(id),
            enabled = enabled,
            names = names,
            ids = ids.map { it -> SomeId(it) },
        )
    }

    companion object {
        fun fromApi(api: SomeClass2): SomeClass2Dto {
            return SomeClass2Dto(
                id = api.id.value,
                enabled = api.enabled,
                names = api.names,
                ids = api.ids.map { it -> it.value },
            )
        }
    }
}

data class SomeClass3Dto(
    val class2Object: SomeClass2Dto,
    val class2List: List<SomeClass2Dto>,
    val someEnum: String,
) {
    fun toApi(): SomeClass3 {
        return SomeClass3(
            class2Object = class2Object.toApi(),
            class2List = class2List.map { it -> it.toApi() },
            someEnum = SomeEnum.valueOf(someEnum),
        )
    }

    companion object {
        fun fromApi(api: SomeClass3): SomeClass3Dto {
            return SomeClass3Dto(
                class2Object = SomeClass2Dto.fromApi(api.class2Object),
                class2List = api.class2List.map { it -> SomeClass2Dto.fromApi(it) },
                someEnum = api.someEnum.name,
            )
        }
    }
}

data class SomeClass4Dto(
    val otherId: String,
    val otherClass: OtherClassDto,
    val otherIdList: List<String>,
    val otherClassList: List<OtherClassDto>,
) {
    fun toApi(): SomeClass4 {
        return SomeClass4(
            otherId = OtherId(otherId),
            otherClass = otherClass.toApi(),
            otherIdList = otherIdList.map { it -> OtherId(it) },
            otherClassList = otherClassList.map { it -> it.toApi() },
        )
    }

    companion object {
        fun fromApi(api: SomeClass4): SomeClass4Dto {
            return SomeClass4Dto(
                otherId = api.otherId.value,
                otherClass = OtherClassDto.fromApi(api.otherClass),
                otherIdList = api.otherIdList.map { it -> it.value },
                otherClassList = api.otherClassList.map { it -> OtherClassDto.fromApi(it) },
            )
        }
    }
}

data class SomeClass5Dto(
    val date: String,
    val dateRange: DateRangeDto,
    val dateRangeWrapper: DateRangeWrapperDto,
    val someProperty: SomeProperty,
    val otherProperty: OtherProperty,
) {
    fun toApi(): SomeClass5 {
        return SomeClass5(
            date = dateCreate(date),
            dateRange = dateRange.toApi(),
            dateRangeWrapper = dateRangeWrapper.toApi(),
            someProperty = someProperty,
            otherProperty = otherProperty,
        )
    }

    companion object {
        fun fromApi(api: SomeClass5): SomeClass5Dto {
            return SomeClass5Dto(
                date = dateGetValue(api.date),
                dateRange = DateRangeDto.fromApi(api.dateRange),
                dateRangeWrapper = DateRangeWrapperDto.fromApi(api.dateRangeWrapper),
                someProperty = api.someProperty,
                otherProperty = api.otherProperty,
            )
        }
    }
}