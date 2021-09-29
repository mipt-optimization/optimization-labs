package ru.sberbank.lab1;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDate.now;
import static java.time.LocalTime.MIN;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.emptyList;

@Service
public class WeatherService {
    private final TemperatureSource temperatureSource;
    private static final long oneDayInSec = 24 * 60 * 60L;

    public WeatherService(TemperatureSource temperatureSource) {
        this.temperatureSource = temperatureSource;
    }

    public List<Double> getTemperatureForLastDays(int days) {
        List<Double> temps = new ArrayList<>();
        // вынес переменные из цикла и сделал их примитивами
        long currentDayInSec = now().toEpochSecond(MIN, UTC);
        try {
            for (int day = 0; day < days; day++) {
                long curDateSec = currentDayInSec - day * oneDayInSec;
                Double curTemp = temperatureSource.getTemperature(Long.toString(curDateSec));
                temps.add(curTemp);
            }
            return temps;
        } catch (JSONException ignored) {
            return emptyList();
        }
    }
}
