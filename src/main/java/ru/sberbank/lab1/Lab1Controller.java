package ru.sberbank.lab1;

import org.json.JSONException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/lab1")
public class Lab1Controller {
    private final TemperatureCacheableService temperatureCacheableService;

    public Lab1Controller(TemperatureCacheableService temperatureCacheableService) {
        this.temperatureCacheableService = temperatureCacheableService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void heatingCache() {
        try {
            getTemperatureForLastDays(300);
        } catch (JSONException e) {
            System.out.println("Cache not heating");
        }
        System.out.println("Cache heating");
    }

    @GetMapping("/weather")
    public List<Double> getWeatherForPeriod(int days) {
        try {
            return getTemperatureForLastDays(days);
        } catch (Exception e) {
            return emptyList();
        }
    }

    public List<Double> getTemperatureForLastDays(int days) throws JSONException {
        LocalDate currentDate = LocalDate.now();
        return IntStream.range(0, days)
                .parallel()
                .mapToObj(currentDate::minusDays)
                .map(temperatureCacheableService::getTemperature)
                .collect(toList());
    }
}

