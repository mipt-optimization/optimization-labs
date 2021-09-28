package ru.sberbank.lab1.repo;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.sberbank.lab1.WeatherRepository;

import java.time.LocalDate;
import java.util.function.Function;

import static java.time.LocalTime.NOON;
import static java.time.ZoneOffset.UTC;

@Component
public class DarkSkyCashedWeatherRepository implements WeatherRepository {

    @Autowired
    Function<Long, String> weatherProducer;

    @Override
    @Cacheable("temperature")
    public Double getTemperatureForDate(LocalDate date) {
        Long epochSeconds = date.toEpochSecond(NOON, UTC);
        String info = weatherProducer.apply(epochSeconds);
        return temperatureFromInfo(info);
    }

    private Double temperatureFromInfo(String info) {
        try {
            return new JSONObject(info)
                    .getJSONObject("hourly")
                    .getJSONArray("data")
                    .getJSONObject(0)
                    .getDouble("temperature");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
