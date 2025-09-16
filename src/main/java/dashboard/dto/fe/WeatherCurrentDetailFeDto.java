package dashboard.dto.fe;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WeatherCurrentDetailFeDto extends WeatherDetailCommonFeDto {
    private Double temperature;
    private Double feelsLike;
    private Double totalPrecipitationMm;
    private Integer dailyChanceOfRain;
    private Double totalSnowCm;
    private Integer dailyChanceOfSnow;
    private Integer pressureMb;
    private Double windKph;
    private String windDirection;
    private Integer aqi;
    private Boolean isDay;
}
