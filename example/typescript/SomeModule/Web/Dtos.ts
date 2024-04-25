namespace SomeModule.Web {
    export class SomeClassDto {
        id = STRING
        amount = NUMBER

        static toApi(dto: SomeClassDto): SomeClass {
            return new SomeClass(
                new SomeId(dto.id),
                dto.amount,
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

        static toApi(dto: SomeClass2Dto): SomeClass2 {
            return new SomeClass2(
                new SomeId(dto.id),
                dto.enabled,
                dto.names,
                dto.ids.map(it => new SomeId(it)),
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

        static toApi(dto: SomeClass3Dto): SomeClass3 {
            return new SomeClass3(
                dto.class2Object.toApi(),
                dto.class2List.map(it => it.toApi()),
            )
        }

        static fromApi(api: SomeClass3): SomeClass3Dto {
            const dto = new SomeClass3Dto()
            dto.class2Object = SomeClass2Dto.fromApi(api.class2Object)
            dto.class2List = api.class2List.map(it => SomeClass2Dto.fromApi(it))
            return dto
        }
    }

    export class SomeClass4Dto {
        otherId = STRING
        otherClass = new OtherModule.Web.OtherClassDto
        otherIdList = [STRING]
        otherClassList = [new OtherModule.Web.OtherClassDto]

        static toApi(dto: SomeClass4Dto): SomeClass4 {
            return new SomeClass4(
                new OtherId(dto.otherId),
                dto.otherClass.toApi(),
                dto.otherIdList.map(it => new OtherId(it)),
                dto.otherClassList.map(it => it.toApi()),
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
}