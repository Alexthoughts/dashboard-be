package dashboard.dto.fe;

import java.time.LocalDateTime;
import java.util.List;

public record WeatherFeDto(
        String region,
        String city,
        LocalDateTime updatedAt,
        WeatherCurrentDetailFeDto current,
        List<WeatherDetailCommonFeDto> forecast
) {
}
