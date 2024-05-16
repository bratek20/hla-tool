namespace SomeModule.Web {
    export class DateRangeWrapperDto {
        range = new TypesModule.Web.DateRangeDto

        toApi(): DateRangeWrapper {
            return CustomTypesMapper.dateRangeWrapperCreate(
                this.range.toApi(),
            )
        }

        static fromApi(api: DateRangeWrapper): DateRangeWrapperDto {
            const dto = new DateRangeWrapperDto()
            dto.range = TypesModule.Web.DateRangeDto.fromApi(CustomTypesMapper.dateRangeWrapperGetRange(api))
            return dto
        }
    }

    export class SomeClassDto {
        id = STRING
        amount = NUMBER

        toApi(): SomeClass {
            return new SomeClass(
                new SomeId(this.id),
                this.amount,
            )
        }

        static fromApi(api: SomeClass): SomeClassDto {
            const dto = new SomeClassDto()
            dto.id = api.id.value
            dto.amount = api.amount
            return dto
        }
    }

    export class SomeClass2Dto {
        id = STRING
        enabled = BOOLEAN
        names = [STRING]
        ids = [STRING]

        toApi(): SomeClass2 {
            return new SomeClass2(
                new SomeId(this.id),
                this.enabled,
                this.names,
                this.ids.map(it => new SomeId(it)),
            )
        }

        static fromApi(api: SomeClass2): SomeClass2Dto {
            const dto = new SomeClass2Dto()
            dto.id = api.id.value
            dto.enabled = api.enabled
            dto.names = api.names
            dto.ids = api.ids.map(it => it.value)
            return dto
        }
    }

    export class SomeClass3Dto {
        class2Object = new SomeClass2Dto
        class2List = [new SomeClass2Dto]
        someEnum = STRING

        toApi(): SomeClass3 {
            return new SomeClass3(
                this.class2Object.toApi(),
                this.class2List.map(it => it.toApi()),
                SomeEnum.fromName(this.someEnum).get(),
            )
        }

        static fromApi(api: SomeClass3): SomeClass3Dto {
            const dto = new SomeClass3Dto()
            dto.class2Object = SomeClass2Dto.fromApi(api.class2Object)
            dto.class2List = api.class2List.map(it => SomeClass2Dto.fromApi(it))
            dto.someEnum = api.someEnum.getName()
            return dto
        }
    }

    export class SomeClass4Dto {
        otherId = STRING
        otherClass = new OtherModule.Web.OtherClassDto
        otherIdList = [STRING]
        otherClassList = [new OtherModule.Web.OtherClassDto]

        toApi(): SomeClass4 {
            return new SomeClass4(
                new OtherId(this.otherId),
                this.otherClass.toApi(),
                this.otherIdList.map(it => new OtherId(it)),
                this.otherClassList.map(it => it.toApi()),
            )
        }

        static fromApi(api: SomeClass4): SomeClass4Dto {
            const dto = new SomeClass4Dto()
            dto.otherId = api.otherId.value
            dto.otherClass = OtherModule.Web.OtherClassDto.fromApi(api.otherClass)
            dto.otherIdList = api.otherIdList.map(it => it.value)
            dto.otherClassList = api.otherClassList.map(it => OtherModule.Web.OtherClassDto.fromApi(it))
            return dto
        }
    }

    export class SomeClass5Dto {
        date = STRING
        dateRange = new TypesModule.Web.DateRangeDto
        dateRangeWrapper = new DateRangeWrapperDto
        someProperty = new SomeProperty
        otherProperty = new OtherProperty

        toApi(): SomeClass5 {
            return new SomeClass5(
                TypesModule.CustomTypesMapper.dateCreate(this.date),
                this.dateRange.toApi(),
                this.dateRangeWrapper.toApi(),
                this.someProperty,
                this.otherProperty,
            )
        }

        static fromApi(api: SomeClass5): SomeClass5Dto {
            const dto = new SomeClass5Dto()
            dto.date = TypesModule.CustomTypesMapper.dateGetValue(api.date)
            dto.dateRange = TypesModule.Web.DateRangeDto.fromApi(api.dateRange)
            dto.dateRangeWrapper = DateRangeWrapperDto.fromApi(api.dateRangeWrapper)
            dto.someProperty = api.someProperty
            dto.otherProperty = api.otherProperty
            return dto
        }
    }
}