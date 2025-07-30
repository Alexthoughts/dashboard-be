package dashboard.mapper;

import dashboard.dto.fe.WeatherCurrentDetailFeDto;
import dashboard.dto.fe.WeatherDetailCommonFeDto;
import dashboard.entity.WeatherEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WeatherMapper {

    WeatherCurrentDetailFeDto weatherEntityToWeatherCurrentDetailFeDto(WeatherEntity entity);

    WeatherDetailCommonFeDto weatherEntityToWeatherDetailCommonFeDto(WeatherEntity entity);

    List<WeatherDetailCommonFeDto> weatherEntityListToWeatherDetailCommonFeDtoList(List<WeatherEntity> entity);
}
