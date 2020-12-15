package ru.sberbank.lab1;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;
import static ru.sberbank.utils.Utils.getZonedDateTimeAtStartOfHour;

@RestController
@RequestMapping("/lab1")
@RequiredArgsConstructor
public class Lab1Controller {
    private final TemperatureProvider temperatureProvider;

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        List<Double> temps = new ArrayList<>();
        ZonedDateTime zonedDateTime = getZonedDateTimeAtStartOfHour();

        for (int i = 0; i < days; i++) {
            long currentDaySeconds = zonedDateTime.minusDays(i).toEpochSecond();
            Double currentTemp = temperatureProvider.getTemperature(valueOf(currentDaySeconds));
            temps.add(currentTemp);
        }
        return temps;
    }
}