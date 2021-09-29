package ru.sberbank.lab1;

import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;
import static java.time.ZoneId.systemDefault;
import static java.util.stream.IntStream.range;

@Service
public class WeatherServiceImpl implements WeatherService {
    //вынес oneDayInSec из цикла (не нужно каждый раз инстанцировать объект)
    private static final long oneDayInSec = 24 * 60 * 60L;
    private final WeatherClient weatherClient;

    public WeatherServiceImpl(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    @Override
    public List<Double> getWeatherForPeriod(int days) {
        //вынес currentDayInSec из цикла (не нужно каждый раз инстанцировать объект)
        long currentDayInSec = now().atStartOfDay(systemDefault()).toEpochSecond();
        return range(0, days).mapToObj(numOfDay -> {
            long curDateSec = currentDayInSec - numOfDay * oneDayInSec;
            String weather = weatherClient.getWeatherFor(curDateSec);
            return extractTemperatureFrom(weather);
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    private Double extractTemperatureFrom(String weather) {
        //избавился от лишних инициализаций JSONObject
        return new JSONObject(weather).getJSONObject("hourly")
                .getJSONArray("data")
                .getJSONObject(0)
                .getDouble("temperature");
    }
}
