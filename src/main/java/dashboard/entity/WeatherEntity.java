package dashboard.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "weather")
public class WeatherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "city")
    private String city;

    @Column(name = "region")
    private String region;

    @Column(name = "temp_c")
    private Double temperature;

    @Column(name = "feels_like")
    private Double feelsLike;

    @Column(name = "max_temperature")
    private Double maxTemperature;

    @Column(name = "min_temperature")
    private Double minTemperature;

    @Column(name = "wind_kph")
    private Double windKph;

    @Column(name = "wind_direction")
    private String windDirection;

    @Column(name = "total_precipitation_mm")
    private Double totalPrecipitationMm;

    @Column(name = "daily_chance_of_rain")
    private Integer dailyChanceOfRain;

    @Column(name = "total_snow_cm")
    private Double totalSnowSm;

    @Column(name = "daily_chance_of_snow")
    private Integer dailyChanceOfSnow;

    @Column(name = "humidity")
    private Integer humidity;

    @Column(name = "pressure_mb")
    private Integer pressureMb;

    @Column(name = "aqi")
    private Integer aqi;

    @Column(name = "is_day")
    private Boolean isDay;

    @Column(name = "icon")
    private String icon;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

