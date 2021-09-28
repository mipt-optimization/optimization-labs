package ru.sberbank.lab1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {

    @Autowired
    WeatherService weatherService;

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        return weatherService.getTemperatureForLastDays(days);
    }

}

