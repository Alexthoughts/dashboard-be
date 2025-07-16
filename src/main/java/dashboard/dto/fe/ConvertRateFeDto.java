package dashboard.dto.fe;

import jakarta.validation.constraints.NotNull;

public record ConvertRateFeDto(
        Long id,

        @NotNull
        String from,

        @NotNull
        String to,

        Double convertedAmount
) {
}
