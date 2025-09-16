package dashboard.dto.fe;

import jakarta.validation.constraints.NotNull;

public record ConvertRateFeDto(
        @NotNull
        String from,

        @NotNull
        String to,

        String convertedAmount
) {
}
