namespace OtherModule.Web {
    export class OtherClassDto {
        id = STRING
        amount = NUMBER

        static toApi(dto: OtherClassDto): OtherClass {
            return new OtherClass(
                new OtherId(dto.id),
                dto.amount,
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