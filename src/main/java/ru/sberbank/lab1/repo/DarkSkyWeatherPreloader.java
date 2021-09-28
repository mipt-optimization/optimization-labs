package ru.sberbank.lab1.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.sberbank.lab1.WeatherRepository;

import java.util.stream.IntStream;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

@Service
public class DarkSkyWeatherPreloader {
    @Autowired
    private WeatherRepository weatherRepository;

    @Value("${weather.preload-days}")
    private int preloadDays;

    @EventListener(ApplicationReadyEvent.class)
    public void preload() {
        IntStream.range(0, preloadDays)
                .parallel()
                .mapToObj(day -> now().minusDays(day))
                .forEach(date -> weatherRepository.getTemperatureForDate(date));
    }
}
