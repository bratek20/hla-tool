namespace SomeModule.Web {
    export class SomeClassDto {
        id: string
        amount: number

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
        id: string
        enabled: boolean
        names: string[]
        ids: string[]

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
        class2Object: SomeClass2Dto
        class2List: SomeClass2Dto[]

        toApi(): SomeClass3 {
            return new SomeClass3(
                this.class2Object.toApi(),
                this.class2List.map(it => it.toApi()),
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
        otherId: string
        otherClass: OtherModule.Web.OtherClassDto
        otherIdList: string[]
        otherClassList: OtherModule.Web.OtherClassDto[]

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
}