namespace OtherModule.Web {
    export class OtherClassDto {
        id = STRING
        amount = NUMBER

        toApi(): OtherClass {
            return new OtherClass(
                new OtherId(this.id),
                this.amount,
            )
        }

        static fromApi(api: OtherClass): OtherClassDto {
            const dto = new OtherClassDto()
            dto.id = api.id.value
            dto.amount = api.amount
            return dto
        }
    }
}