package dashboard.controller;

import dashboard.dto.fe.ResponseFeDTO;
import dashboard.dto.fe.WeatherFeDto;
import dashboard.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {
        private final WeatherService weatherService;

    @GetMapping("/get-weather")
    public ResponseFeDTO<WeatherFeDto> getWeather(@RequestParam Double lat, @RequestParam Double lon) {
       return weatherService.getWeather(lat, lon);
    }
}
