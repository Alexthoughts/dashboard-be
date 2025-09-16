package dashboard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import dashboard.client.AqiApiClient;
import dashboard.client.WeatherApiClient;
import dashboard.dto.fe.ResponseFeDTO;
import dashboard.dto.fe.WeatherCurrentDetailFeDto;
import dashboard.dto.fe.WeatherDetailCommonFeDto;
import dashboard.dto.fe.WeatherFeDto;
import dashboard.entity.WeatherEntity;
import dashboard.mapper.WeatherMapper;
import dashboard.repository.WeatherRepository;
import dashboard.util.HelperMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherMapper weatherMapper;
    private final WeatherRepository weatherRepository;
    private final Integer forecastDays = 3;
    private final HelperMethods helperMethods;
    private final WeatherApiClient weatherApiClient;
    private final AqiApiClient aqiApiClient;

    public ResponseFeDTO<WeatherFeDto> getWeather(Double lat, Double lon) {
        List<String> apiResponseErrorsList = new ArrayList<>();
        ResponseFeDTO<WeatherFeDto> weatherResponse = new ResponseFeDTO<>();

        JsonNode responseWeather = getWeatherFromApi(lat, lon, apiResponseErrorsList);
        JsonNode responseAqi = getAqiFromApi(lat, lon, apiResponseErrorsList);

        List<WeatherEntity> existingEntities = weatherRepository.findAll();

        if (responseWeather.isEmpty() && existingEntities.isEmpty()) {
            weatherResponse.setErrors(apiResponseErrorsList);
            return weatherResponse;
        }

        long epochSeconds = responseWeather.path("current").path("last_updated_epoch").asLong();
        LocalDateTime updatedAt = helperMethods.convertEpochSecondsToLocalDateTime(epochSeconds);

        if (responseWeather.isEmpty() && !existingEntities.isEmpty()) {
            return createWeatherResponseObject(existingEntities, updatedAt, weatherResponse, apiResponseErrorsList);
        }

        List<WeatherEntity> entitiesToSave = createWeatherEntity(existingEntities, responseWeather, responseAqi, updatedAt);

        weatherRepository.saveAll(entitiesToSave);

        return createWeatherResponseObject(entitiesToSave, updatedAt, weatherResponse, apiResponseErrorsList);
    }

    private ResponseFeDTO<WeatherFeDto> createWeatherResponseObject(List<WeatherEntity> entityList, LocalDateTime updatedAt,
                                                                    ResponseFeDTO<WeatherFeDto> weatherResponse, List<String> apiResponseErrorsList) {
        List<WeatherDetailCommonFeDto> weatherForecastList =
                weatherMapper.weatherEntityListToWeatherDetailCommonFeDtoList(entityList.subList(1, entityList.size()));
        WeatherCurrentDetailFeDto currentWeather = weatherMapper.weatherEntityToWeatherCurrentDetailFeDto(entityList.get(0));
        String region = entityList.get(0).getRegion();
        String city = entityList.get(0).getCity();

        WeatherFeDto weather = new WeatherFeDto(region, city, updatedAt, currentWeather, weatherForecastList);
        weatherResponse.setData(weather);
        weatherResponse.setErrors(apiResponseErrorsList);

        return weatherResponse;
    }

    private List<WeatherEntity> createWeatherEntity(List<WeatherEntity> dbList, JsonNode responseWeather, JsonNode responseAqi, LocalDateTime updatedAt) {
        for (int i = 0; i < forecastDays; i++) {
            WeatherEntity entity;

            if (i < dbList.size()) {
                entity = dbList.get(i);
            } else {
                entity = new WeatherEntity();
                dbList.add(entity);
            }

            if (i == 0) {
                JsonNode current = responseWeather.path("current");

                entity.setTemperature(current.path("temp_c").asDouble());
                entity.setIsDay(current.path("is_day").asInt() == 1);
                entity.setIcon(current.path("condition").path("icon").asText());
                entity.setWindKph(current.path("wind_kph").asDouble());
                entity.setWindDirection(current.path("wind_dir").asText());
                entity.setFeelsLike(current.path("feelslike_c").asDouble());
                entity.setHumidity(current.path("humidity").asInt());
                entity.setPressureMb(current.path("pressure_mb").asInt());

                if (!responseAqi.isEmpty()) {
                    entity.setAqi(responseAqi.path("data").get(0).path("aqi").asInt());
                }
            }

            JsonNode forecastDay = responseWeather.path("forecast").path("forecastday").get(i).path("day");

            if (i != 0) {
                entity.setIcon(forecastDay.path("condition").path("icon").asText());
            }
            JsonNode location = responseWeather.path("location");

            entity.setCity(location.path("name").asText());
            entity.setRegion(location.path("region").asText());
            entity.setMaxTemperature(forecastDay.path("maxtemp_c").asDouble());
            entity.setMinTemperature(forecastDay.path("mintemp_c").asDouble());
            entity.setTotalPrecipitationMm(forecastDay.path("totalprecip_mm").asDouble());
            entity.setTotalSnowCm(forecastDay.path("totalsnow_cm").asDouble());
            entity.setDailyChanceOfRain(forecastDay.path("daily_chance_of_rain").asInt());
            entity.setDailyChanceOfSnow(forecastDay.path("daily_chance_of_snow").asInt());
            entity.setUpdatedAt(updatedAt);
        }

        return dbList;
    }

    private JsonNode getWeatherFromApi(Double lat, Double lon, List<String> apiResponseErrorsList) {
        try {
            return weatherApiClient.fetchWeather(lat, lon, forecastDays);

        } catch (RestClientResponseException ex) {
            apiResponseErrorsList.add("Weather API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
            log.error("Weather API error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            apiResponseErrorsList.add("Weather API call failed: " + ex.getMessage() + ", it could be a bad request");
            log.error("Weather API call failed: {}, it could be a bad request", ex.getMessage());
        }
        return NullNode.getInstance();
    }

    private JsonNode getAqiFromApi(Double lat, Double lon, List<String> apiResponseErrorsList) {
        try {
            return aqiApiClient.fetchAqi(lat, lon);

        } catch (RestClientResponseException ex) {
            apiResponseErrorsList.add("Air quality API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
            log.error("Air quality API error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            apiResponseErrorsList.add("Air quality API call failed: " + ex.getMessage() + ", it could be a bad request");
            log.error("Air quality API call failed: {}, it could be a bad request", ex.getMessage(), ex);
        }
        return NullNode.getInstance();
    }
}
