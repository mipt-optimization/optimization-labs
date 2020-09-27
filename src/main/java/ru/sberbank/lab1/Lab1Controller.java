package ru.sberbank.lab1;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Calendar.getInstance;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

@RestController
@RequestMapping("/lab1")
@RequiredArgsConstructor
public class Lab1Controller {
    //Using cached temperature source
    @Qualifier("cachedTemperatureSource")
    private final TemperatureSource temperatureSource;

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(Integer days) {
        return getTemperatureForLastDays(days);
    }

    public List<Double> getTemperatureForLastDays(int days) {
        return range(0, days)
                .mapToLong(this::hourTimestampNDaysAgo)
                .mapToObj(temperatureSource::get)
                .collect(toList());
    }

    private long hourTimestampNDaysAgo(int n) {
        return getInstance().getTimeInMillis() / (1000 * 60 * 60) - n * 24;
    }
}

