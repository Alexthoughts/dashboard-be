package dashboard.mapper;

import dashboard.dto.fe.ConvertRateFeDto;
import dashboard.dto.fe.SupportedCurrenciesFeDto;
import dashboard.entity.ConvertRateEntity;
import dashboard.entity.SupportedCurrenciesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConvertRateMapper {

    @Mapping(source = "fromCurrencyId.symbol", target = "from")
    @Mapping(source = "toCurrencyId.symbol", target = "to")
    ConvertRateFeDto fromConverRateEntityToConvertRateFeDto(ConvertRateEntity convertRateEntity);

    List<ConvertRateFeDto> fromConvertRateEntityListToConvertRateFeDtoList(List<ConvertRateEntity> convertRateEntity);

    List<SupportedCurrenciesEntity> fromSupportedCurrenciesFeDtoListToSupportedCurrenciesEntityList(List<SupportedCurrenciesFeDto> supportedCurrenciesList);

    SupportedCurrenciesEntity fromSupportedCurrenciesEntityToSupportedCurrenciesFeDto(SupportedCurrenciesFeDto supportedCurrencies);

    List<SupportedCurrenciesFeDto> fromSupportedCurrenciesEntityListToSupportedCurrenciesFeDtoList(List<SupportedCurrenciesEntity> supportedCurrenciesEntities);

    @Mapping(target = "id", ignore = true)
    SupportedCurrenciesFeDto fromSupportedCurrenciesEntityToSupportedCurrenciesFeDto(SupportedCurrenciesEntity supportedCurrenciesEntity);
}
