package dashboard.mapper;

import dashboard.dto.fe.HolidayFeDto;
import dashboard.dto.HolidayResponseDto;
import dashboard.entity.HolidayEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HolidayMapper {

    @Mapping(target = "id", ignore = true)
    HolidayEntity holidayResponseDtoToHolidayEntity(HolidayResponseDto holidayResponseDto);

    List<HolidayEntity> holidayResponseDtoListToHolidayEntityList(List<HolidayResponseDto> holidayResponseDtoList);

    HolidayFeDto holidayEntityToHolidayFeDto(HolidayEntity holidayEntity);

    List<HolidayFeDto> holidayEntityListToHolidayFeDtoList(List<HolidayEntity> holidayEntityList);


}