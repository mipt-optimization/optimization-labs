package ru.sberbank.lab1;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {

    @Autowired
    WeatherService weatherService;

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        try {
            return weatherService.getTemperatureForLastDays(days);
        } catch (JSONException e) {
        }

        return emptyList();
    }

}

