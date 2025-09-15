package dashboard.dto.fe;

import lombok.Data;

@Data
public class WeatherDetailCommonFeDto {
    private Long id;
    private Double maxTemperature;
    private Double minTemperature;
    private Double totalPrecipitationMm;
    private Integer dailyChanceOfRain;
    private Double totalSnowCm;
    private Integer dailyChanceOfSnow;
    private String icon;
}
