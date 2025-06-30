package dashboard.mapper;

import dashboard.dto.HolidayFeDto;
import dashboard.dto.HolidayResponseDto;
import dashboard.entity.HolidayEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HolidayMapper {
    HolidayEntity holidayResponseDtoToHolidayEntity(HolidayResponseDto holidayResponseDto);

    List<HolidayEntity> holidayResponseDtoListToHolidayEntityList(List<HolidayResponseDto> holidayResponseDtoList);

    HolidayFeDto holidayEntityToHolidayFeDto(HolidayEntity holidayEntity);

    List<HolidayFeDto> holidayEntityListToHolidayFeDtoList(List<HolidayEntity> holidayEntityList);


}