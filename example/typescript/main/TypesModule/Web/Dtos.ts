namespace TypesModule.Web {
    export class DateRangeDto {
        from = STRING
        to = NUMBER

        static toApi(dto: DateRangeDto): DateRange {
            return CustomTypesMapper.createDateRange(
                CustomTypesMapper.createDate(dto.from),
                CustomTypesMapper.createDate(dto.to)
            )
        }

        static fromApi(api: DateRange): DateRangeDto {
            const dto = new DateRangeDto()
            dto.from = CustomTypesMapper.getDateValue(CustomTypesMapper.dateRangeGetFrom(api))
            dto.to = CustomTypesMapper.getDateValue(CustomTypesMapper.dateRangeGetTo(api))
            return dto
        }
    }
}