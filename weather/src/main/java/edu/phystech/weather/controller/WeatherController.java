package edu.phystech.weather.controller;

import java.util.List;

import edu.phystech.weather.domain.Weather;
import edu.phystech.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {
    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/")
    public List<Weather> getWeatherForLastDays(@RequestParam(required = false) Integer days,
                                               @RequestParam(required = false) String city) {
        int numberOfDays = 1;
        if (days != null) {
            numberOfDays = days;
        }
        if (city == null) {
            return weatherService.getWeatherForLastDays(numberOfDays);
        }
        return weatherService.getWeatherForLastDays(numberOfDays, city);
    }

}
