package ru.sberbank.lab1;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.Duration.ofMinutes;
import static java.time.ZonedDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.IntStream.range;

@Service
public class WeatherServiceImpl implements WeatherService {
    private final DarkskyClient darkskyClient;
    private final Cache<String, Double> weatherCache = Caffeine.newBuilder()
            .expireAfterWrite(ofMinutes(1))
            .maximumSize(50)
            .build();

    public WeatherServiceImpl(DarkskyClient darkskyClient) {
        this.darkskyClient = darkskyClient;
    }

    @Override
    public List<Double> getTemperatureForLastDays(int days) {
        var currentDateTime = now().truncatedTo(MINUTES);
        return range(-days + 1, 1).parallel()
                .mapToObj(currentDateTime::minusDays)
                .map(ZonedDateTime::toEpochSecond)
                .map(String::valueOf)
                .map(date -> weatherCache.get(date, this::getTemperatureFromInfo))
                .collect(toUnmodifiableList());
    }

    private String getTodayWeather(String date) {
        String info = darkskyClient.get(date).block();
        System.out.println(info);
        return info;
    }

    private Double getTemperatureFromInfo(String date) {
        try {
            return getTemperature(getTodayWeather(date));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private Double getTemperature(String info) throws JSONException {
        return new JSONObject(info)
                .getJSONObject("hourly")
                .getJSONArray("data")
                .getJSONObject(0)
                .getDouble("temperature");
    }
}
