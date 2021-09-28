package ru.sberbank.lab1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sberbank.lab1.WeatherRepository;
import ru.sberbank.lab1.WeatherService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.toList;

@Service
public class WeatherServiceImp implements WeatherService {

    @Autowired
    WeatherRepository weatherRepository;

    public List<Double> getTemperatureForLastDays(int days) {
        return IntStream.range(0, days)
                .parallel()
                .mapToObj(this::dateBeforeDays)
                .map(weatherRepository::getTemperatureForDate)
                .collect(toList());
    }

    private LocalDate dateBeforeDays(int days) {
        return now().minusDays(days);
    }
}
