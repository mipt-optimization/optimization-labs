package ru.sberbank.lab1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {
    private final WeatherServiceImpl weatherService;

    public Lab1Controller(WeatherServiceImpl weatherSource) {
        this.weatherService = weatherSource;
    }

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(int days) {
        return weatherService.getWeatherForPeriod(days);
    }
}

