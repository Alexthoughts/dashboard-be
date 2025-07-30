package dashboard.dto.fe;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class WeatherCurrentDetailFeDto extends WeatherDetailCommonFeDto {
    private Double feelsLike;
    private Double totalPrecipitationMm;
    private Integer dailyChanceOfRain;
    private Double totalSnowSm;
    private Integer dailyChanceOfSnow;
    private Integer pressureMb;
    private Integer aqi;
    private Boolean isDay;
}
