namespace TypesModule.Web {
    export class DateRangeDto {
        from = STRING
        to = STRING

        static toApi(dto: DateRangeDto): DateRange {
            return CustomTypesMapper.createDateRange(
                CustomTypesMapper.createDate(dto.from),
                CustomTypesMapper.createDate(dto.to),
            )
        }

        static fromApi(api: DateRange): DateRangeDto {
            const dto = new DateRangeDto()
            dto.from = CustomTypesMapper.getDateValue(CustomTypesMapper.getDateRangeFrom(api))
            dto.to = CustomTypesMapper.getDateValue(CustomTypesMapper.getDateRangeTo(api))
            return dto
        }
    }
}