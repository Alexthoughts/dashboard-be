package dashboard.dto.fe;

import java.util.List;
import java.util.Optional;

public record UpdateSavedRatesFeDto(
        List<ConvertRateFeDto> convertRateList,
        Optional<Boolean> isUpdated
) {
}
